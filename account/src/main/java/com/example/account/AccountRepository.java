package com.example.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Account> findWithLockById(Long id);

}
