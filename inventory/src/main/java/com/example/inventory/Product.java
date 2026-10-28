package com.example.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private Integer quantity;

	public Product(String name, long price, int quantity) {
		if (price <= 0) {
			throw new IllegalArgumentException("가격은 0보다 커야 합니다. price=" + price);
		}
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}

	public void decrease(int amount) {
		if (quantity < amount) {
			throw new IllegalStateException("재고가 부족합니다. productId=" + id);
		}
		this.quantity = quantity - amount;
	}

	public void increase(int amount) {
		this.quantity = quantity + amount;
	}

}
