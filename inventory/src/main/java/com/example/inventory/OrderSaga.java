package com.example.inventory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.contract.shipping.ShippingReply;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSaga {

	// 타임아웃 + 커넥션 대기
	private static final long STALE_SECONDS = 35;

	private final OrderService orderService;
	private final ProductService productService;
	private final AccountClient accountClient;

	public Long start(Long productId, int quantity, Long accountId, String address) {
		// 재고 (로컬)
		Order order = orderService.createAndDecreaseStock(productId, quantity, accountId, address);
		if (order.getStatus() == OrderStatus.FAILED) {
			log.warn("재고 차감 실패. orderId={}, productId={}", order.getId(), productId);
			throw new IllegalStateException("재고가 부족합니다. productId=" + productId);
		}

		// 출금 (gRPC)
		try {
			accountClient.withdraw(order.getId(), order.getAccountId(), order.getAmount());
		} catch (StatusRuntimeException e) {
			log.warn("출금 실패. orderId={}, status={}", order.getId(), e.getStatus());
			orderService.changeStatus(order.getId(), OrderStatus.STOCK_DECREASED, OrderStatus.ACCOUNT_COMPENSATING);
			compensate(order.getId());

			// 비즈니스 실패
			Status.Code code = e.getStatus().getCode();
			if (code == Status.Code.INVALID_ARGUMENT || code == Status.Code.FAILED_PRECONDITION) {
				throw new IllegalStateException(e.getStatus().getDescription());
			}
			throw e;
		}

		// 배송 (Kafka)
		orderService.requestShipping(order.getId());
		return order.getId();
	}

	@KafkaListener(topics = ShippingReply.TOPIC)
	public void handle(ShippingReply reply) {
		if (reply instanceof ShippingReply.Created) {
			orderService.changeStatus(reply.orderId(), OrderStatus.SHIPPING_REQUESTED, OrderStatus.COMPLETED);
		} else if (reply instanceof ShippingReply.Failed(Long orderId, String reason)) {
			log.warn("배송 실패. orderId={}, reason={}", orderId, reason);
		} // 기타 에러는 Kafka 재시도
	}

	// 배치 재시도
	@Scheduled(fixedDelay = 10000)
	public void retryCompensation() {
		LocalDateTime threshold = LocalDateTime.now().minusSeconds(STALE_SECONDS);

		// 출금 단계
		List<Order> withdrawing = orderService.findByStatusAndModifiedAtBefore(OrderStatus.STOCK_DECREASED, threshold);
		for (Order order : withdrawing) {
			try {
				orderService.changeStatus(order.getId(), OrderStatus.STOCK_DECREASED, OrderStatus.ACCOUNT_COMPENSATING);
				compensate(order.getId());
			} catch (RuntimeException e) {
				// 장애 격리
				log.warn("보상 전환 실패. orderId={}", order.getId(), e);
			}
		}

		// 보상 단계
		List<Order> compensating = orderService.findByStatus(OrderStatus.ACCOUNT_COMPENSATING);
		for (Order order : compensating) {
			compensate(order.getId());
		}
	}

	// 배치 재시도
	@Scheduled(fixedDelay = 10000)
	public void retryShipping() {
		LocalDateTime threshold = LocalDateTime.now().minusSeconds(STALE_SECONDS);

		// 배송 응답 대기 단계
		// 에러가 반복되는 경우 수동 처리
		List<Order> requested = orderService.findByStatusAndModifiedAtBefore(OrderStatus.SHIPPING_REQUESTED, threshold);
		for (Order order : requested) {
			try {
				orderService.resendShipping(order.getId());
			} catch (RuntimeException e) {
				// 장애 격리
				log.warn("배송 재발행 실패. orderId={}", order.getId(), e);
			}
		}
	}

	private void compensate(Long orderId) {
		try {
			Order order = orderService.get(orderId);
			accountClient.refund(orderId, order.getAccountId(), order.getAmount());
			productService.increase(orderId);
		} catch (RuntimeException e) {
			log.warn("보상 실패. orderId={}", orderId, e);
		}
	}

}
