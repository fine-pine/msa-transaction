package com.example.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;

	@Transactional
	public boolean decrease(Long productId, int quantity) {
		// 락 획득
		Product product = findProduct(productId);

		// 재고 확인
		if (product.getQuantity() < quantity) {
			return false;
		}

		// 재고 차감
		product.decrease(quantity);
		return true;
	}

	@Transactional
	public void increase(Long orderId) {
		// 락 획득
		Order order = findOrder(orderId);

		// 멱등 처리
		if (order.getStatus() != OrderStatus.ACCOUNT_COMPENSATING) {
			return;
		}

		// 재고 복구
		increase(order.getProductId(), order.getQuantity());
		order.setStatus(OrderStatus.FAILED);
	}

	@Transactional
	public void increase(Long productId, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 0보다 커야 합니다. quantity=" + quantity);
		}
		Product product = findProduct(productId);
		product.increase(quantity);
	}

	private Order findOrder(Long orderId) {
		return orderRepository.findWithLockById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다. orderId=" + orderId));
	}

	private Product findProduct(Long productId) {
		return productRepository.findWithLockById(productId)
				.orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. productId=" + productId));
	}

}
