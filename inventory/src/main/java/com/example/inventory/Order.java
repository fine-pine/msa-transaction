package com.example.inventory;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime modifiedAt;

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

}
