package com.example.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "order_id", "type" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long orderId;

	@Column(nullable = false)
	private Long accountId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountTransactionType type;

	@Column(nullable = false)
	private Long amount;

	public AccountTransaction(Long orderId, Long accountId, AccountTransactionType type, long amount) {
		this.orderId = orderId;
		this.accountId = accountId;
		this.type = type;
		this.amount = amount;
	}

}
