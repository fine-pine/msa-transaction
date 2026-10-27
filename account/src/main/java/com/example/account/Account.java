package com.example.account;

import java.math.BigDecimal;

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
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal balance;

	public void withdraw(BigDecimal amount) {
		if (balance.compareTo(amount) < 0) {
			throw new IllegalStateException("잔액이 부족합니다. accountId=" + id);
		}
		this.balance = balance.subtract(amount);
	}

	public void deposit(BigDecimal amount) {
		this.balance = balance.add(amount);
	}

}
