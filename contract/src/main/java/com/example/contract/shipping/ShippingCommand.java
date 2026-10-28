package com.example.contract.shipping;

public sealed interface ShippingCommand {

	String TOPIC = "shipping.command";

	Long orderId();

	record Create(Long orderId, String address) implements ShippingCommand {
	}

	record Cancel(Long orderId, Long shipmentId) implements ShippingCommand {
	}

}
