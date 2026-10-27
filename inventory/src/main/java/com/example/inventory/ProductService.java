package com.example.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	@Transactional
	public void decrease(Long productId, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 0보다 커야 합니다. quantity=" + quantity);
		}
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. productId=" + productId));
		product.decrease(quantity);
	}

	@Transactional
	public void increase(Long productId, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 0보다 커야 합니다. quantity=" + quantity);
		}
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. productId=" + productId));
		product.increase(quantity);
	}

}
