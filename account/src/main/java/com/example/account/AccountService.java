package com.example.account;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	@Transactional
	public void withdraw(Long accountId, BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("금액은 0보다 커야 합니다. amount=" + amount);
		}
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new IllegalArgumentException("계좌가 없습니다. accountId=" + accountId));
		account.withdraw(amount);
	}

	@Transactional
	public void refund(Long accountId, BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("금액은 0보다 커야 합니다. amount=" + amount);
		}
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new IllegalArgumentException("계좌가 없습니다. accountId=" + accountId));
		account.deposit(amount);
	}

}
