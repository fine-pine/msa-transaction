package com.example.account;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final AccountTransactionRepository accountTransactionRepository;

	@Transactional
	public void withdraw(Long orderId, Long accountId, long amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("금액은 0보다 커야 합니다. amount=" + amount);
		}

		// 락 획득
		Account account = accountRepository.findWithLockById(accountId)
				.orElseThrow(() -> new IllegalArgumentException("계좌가 없습니다. accountId=" + accountId));

		// 상태 확인
		if (accountTransactionRepository.existsByOrderIdAndType(orderId, AccountTransactionType.CANCEL)) {
			throw new IllegalStateException("취소된 출금입니다. orderId=" + orderId);
		}

		// 멱등 처리
		if (accountTransactionRepository.existsByOrderIdAndType(orderId, AccountTransactionType.WITHDRAW)) {
			return;
		}

		// 출금
		account.withdraw(amount);
		AccountTransaction transaction = new AccountTransaction(orderId, accountId, AccountTransactionType.WITHDRAW, amount);
		accountTransactionRepository.save(transaction);
	}

	@Transactional
	public void refund(Long orderId, Long accountId, long amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("금액은 0보다 커야 합니다. amount=" + amount);
		}

		// 락 획득
		Optional<Account> found = accountRepository.findWithLockById(accountId);

		// 멱등 처리
		if (accountTransactionRepository.existsByOrderIdAndType(orderId, AccountTransactionType.REFUND)
				|| accountTransactionRepository.existsByOrderIdAndType(orderId, AccountTransactionType.CANCEL)) {
			return;
		}

		Optional<AccountTransaction> withdrawal = accountTransactionRepository.findByOrderIdAndType(orderId, AccountTransactionType.WITHDRAW);
		// 출금 전 환불
		// 존재하지 않는 계좌에 대한 보상 트랜잭션 성공
		if (withdrawal.isEmpty()) {
			AccountTransaction cancel = new AccountTransaction(orderId, accountId, AccountTransactionType.CANCEL, amount);
			accountTransactionRepository.save(cancel);
			return;
		}

		// 출금 후 환불
		// 출금 이후 삭제된 계좌에 대한 보상 트랜잭션 실패 (추후 수동 환불)
		Account account = found
				.orElseThrow(() -> new IllegalArgumentException("계좌가 없습니다. accountId=" + accountId));
		long withdrawnAmount = withdrawal.get().getAmount();
		account.deposit(withdrawnAmount);
		AccountTransaction refund = new AccountTransaction(orderId, accountId, AccountTransactionType.REFUND, withdrawnAmount);
		accountTransactionRepository.save(refund);
	}

}
