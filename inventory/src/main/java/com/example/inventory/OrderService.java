package com.example.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.contract.shipping.ShippingCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final ProductService productService;
	private final OutboxService outboxService;

	@Transactional
	public Order createAndDecreaseStock(Long productId, int quantity, Long accountId, String address) {
		// 상품 조회
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. productId=" + productId));

		// 주문 생성
		long amount = Math.multiplyExact(product.getPrice(), quantity);
		Order order = new Order(productId, quantity, accountId, amount, address);

		// 재고 차감
		boolean decreased = productService.decrease(productId, quantity);

		// 상태 변경
		if (decreased) {
			order.setStatus(OrderStatus.STOCK_DECREASED);
		} else {
			order.setStatus(OrderStatus.FAILED);
		}
		orderRepository.save(order);
		return order;
	}

	@Transactional
	public void changeStatus(Long orderId, OrderStatus from, OrderStatus to) {
		Order order = orderRepository.findWithLockById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다. orderId=" + orderId));

		// 멱등 처리
		if (order.getStatus() == to) {
			return;
		}

		// 상태 확인
		if (order.getStatus() != from) {
			throw new IllegalStateException("주문 상태가 맞지 않습니다. orderId=" + orderId);
		}
		order.setStatus(to);
	}

	@Transactional
	public void requestShipping(Long orderId) {
		// 락 획득
		Order order = orderRepository.findWithLockById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다. orderId=" + orderId));

		// 멱등 처리
		if (order.getStatus() == OrderStatus.SHIPPING_REQUESTED) {
			return;
		}

		// 상태 확인
		if (order.getStatus() != OrderStatus.ACCOUNT_WITHDRAWN) {
			throw new IllegalStateException("주문 상태가 맞지 않습니다. orderId=" + orderId);
		}
		order.setStatus(OrderStatus.SHIPPING_REQUESTED);

		// 메시지 발행
		ShippingCommand command = new ShippingCommand.Create(orderId, order.getAddress());
		outboxService.append(ShippingCommand.TOPIC, orderId, ShippingCommand.Create.TYPE, command);
	}

	@Transactional(readOnly = true)
	public Order get(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다. orderId=" + orderId));
	}

}
