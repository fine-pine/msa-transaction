package com.example.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

	boolean existsByOrderIdAndType(Long orderId, AccountTransactionType type);

	Optional<AccountTransaction> findByOrderIdAndType(Long orderId, AccountTransactionType type);

}
