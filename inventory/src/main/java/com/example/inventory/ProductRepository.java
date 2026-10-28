package com.example.inventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Product> findWithLockById(Long id);

}
