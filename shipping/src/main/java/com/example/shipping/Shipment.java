package com.example.shipping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long orderId;

	@Column(nullable = false)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ShipmentStatus status;

	public Shipment(Long orderId, String address) {
		this.orderId = orderId;
		this.address = address;
		this.status = ShipmentStatus.REQUESTED;
	}

	public void cancel() {
		this.status = ShipmentStatus.CANCELLED;
	}

}
