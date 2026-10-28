package com.example.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long productId;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Long accountId;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	public Order(Long productId, int quantity, Long accountId, long amount, String address) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 0보다 커야 합니다. quantity=" + quantity);
		}
		if (address == null || address.isBlank()) {
			throw new IllegalArgumentException("배송지가 비어 있습니다.");
		}
		this.productId = productId;
		this.quantity = quantity;
		this.accountId = accountId;
		this.amount = amount;
		this.address = address;
		this.status = OrderStatus.STARTED;
	}

	public void changeStatus(OrderStatus status) {
		this.status = status;
	}

}
