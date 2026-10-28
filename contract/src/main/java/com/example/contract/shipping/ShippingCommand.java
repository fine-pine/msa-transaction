package com.example.contract.shipping;

public sealed interface ShippingCommand {

	String TOPIC = "shipping.command";

	record Create(String address) implements ShippingCommand {
	}

	record Cancel(Long shipmentId) implements ShippingCommand {
	}

}
