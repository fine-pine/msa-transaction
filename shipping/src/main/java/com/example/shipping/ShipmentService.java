package com.example.shipping;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentService {

	private final ShipmentRepository shipmentRepository;

	@Transactional
	public Long create(Long orderId, String address) {
		if (address == null || address.isBlank()) {
			throw new IllegalArgumentException("배송지가 비어 있습니다.");
		}

		// 멱등 처리
		Optional<Shipment> existing = shipmentRepository.findByOrderId(orderId);
		if (existing.isPresent()) {
			return existing.get().getId();
		}

		// 배송 생성
		Shipment shipment = new Shipment(orderId, address);
		shipmentRepository.save(shipment);
		return shipment.getId();
	}

	@Transactional
	public void cancel(Long shipmentId) {
		Shipment shipment = shipmentRepository.findById(shipmentId)
				.orElseThrow(() -> new IllegalArgumentException("배송이 없습니다. shipmentId=" + shipmentId));
		shipment.cancel();
	}

}
