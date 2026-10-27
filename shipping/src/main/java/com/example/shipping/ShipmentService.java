package com.example.shipping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentService {

	private final ShipmentRepository shipmentRepository;

	@Transactional
	public Long create(String address) {
		if (address == null || address.isBlank()) {
			throw new IllegalArgumentException("배송지가 비어 있습니다.");
		}
		Shipment shipment = shipmentRepository.save(new Shipment(address));
		return shipment.getId();
	}

	@Transactional
	public void cancel(Long shipmentId) {
		Shipment shipment = shipmentRepository.findById(shipmentId)
				.orElseThrow(() -> new IllegalArgumentException("배송이 없습니다. shipmentId=" + shipmentId));
		shipment.cancel();
	}

}
