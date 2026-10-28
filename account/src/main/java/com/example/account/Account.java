package com.example.account;

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

	@Column(nullable = false)
	private Long balance;

	public void withdraw(long amount) {
		if (balance < amount) {
			throw new IllegalStateException("잔액이 부족합니다. accountId=" + id);
		}
		this.balance = balance - amount;
	}

	public void deposit(long amount) {
		this.balance = Math.addExact(balance, amount);
	}

}
