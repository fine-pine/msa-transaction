package com.example.inventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Order> findWithLockById(Long id);

}
