package com.example.contract.shipping;

public sealed interface ShippingReply {

	String TOPIC = "shipping.reply";

	record Created(Long shipmentId) implements ShippingReply {
	}

	record Cancelled(Long shipmentId) implements ShippingReply {
	}

	record Failed(String reason) implements ShippingReply {
	}

}
