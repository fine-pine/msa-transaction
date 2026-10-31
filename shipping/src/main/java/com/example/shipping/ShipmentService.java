package com.example.shipping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.contract.shipping.ShippingReply;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentService {

	private final ShipmentRepository shipmentRepository;
	private final OutboxService outboxService;

	@Transactional
	public void create(Long orderId, String address) {
		if (address == null || address.isBlank()) {
			throw new IllegalArgumentException("배송지가 비어 있습니다.");
		}

		// 멱등 처리
		Shipment shipment = shipmentRepository.findByOrderId(orderId).orElse(null);

		// 배송 생성
		if (shipment == null) {
			shipment = new Shipment(orderId, address);
			shipmentRepository.save(shipment);
		}

		// 메시지 발행
		ShippingReply reply = new ShippingReply.Created(orderId, shipment.getId());
		outboxService.append(ShippingReply.TOPIC, orderId, ShippingReply.Created.TYPE, reply);
	}

	@Transactional
	public void cancel(Long shipmentId) {
		Shipment shipment = shipmentRepository.findById(shipmentId)
				.orElseThrow(() -> new IllegalArgumentException("배송이 없습니다. shipmentId=" + shipmentId));
		shipment.cancel();
		ShippingReply reply = new ShippingReply.Cancelled(shipment.getOrderId(), shipmentId);
		outboxService.append(ShippingReply.TOPIC, shipment.getOrderId(), ShippingReply.Cancelled.TYPE, reply);
	}

	@Transactional
	public void fail(Long orderId, String reason) {
		ShippingReply reply = new ShippingReply.Failed(orderId, reason);
		outboxService.append(ShippingReply.TOPIC, orderId, ShippingReply.Failed.TYPE, reply);
	}

}
