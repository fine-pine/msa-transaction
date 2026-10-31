package com.example.inventory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.contract.shipping.ShippingReply;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSaga {

	private final OrderService orderService;
	private final ProductService productService;
	private final AccountClient accountClient;

	public Long start(Long productId, int quantity, Long accountId, String address) {
		// 재고 (로컬)
		Order order = orderService.createAndDecreaseStock(productId, quantity, accountId, address);
		if (order.getStatus() == OrderStatus.FAILED) {
			log.warn("재고 차감 실패. orderId={}, productId={}", order.getId(), productId);
			return order.getId();
		}

		// 출금 (gRPC)
		try {
			accountClient.withdraw(order.getId(), order.getAccountId(), order.getAmount());
		} catch (StatusRuntimeException e) {
			log.warn("출금 실패. orderId={}, status={}", order.getId(), e.getStatus());
			orderService.changeStatus(order.getId(), OrderStatus.STOCK_DECREASED, OrderStatus.ACCOUNT_COMPENSATING);
			compensate(order.getId());
			return order.getId();
		}
		orderService.changeStatus(order.getId(), OrderStatus.STOCK_DECREASED, OrderStatus.ACCOUNT_WITHDRAWN);

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
		}
	}

	private void compensate(Long orderId) {
		Order order = orderService.get(orderId);
		try {
			accountClient.refund(orderId, order.getAccountId(), order.getAmount());
			productService.increase(orderId);
		} catch (RuntimeException e) {
			log.warn("보상 실패. orderId={}", orderId, e);
		}
	}

}
