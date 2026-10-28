package com.example.contract.shipping;

public sealed interface ShippingReply {

	String TOPIC = "shipping.reply";

	Long orderId();

	record Created(Long orderId, Long shipmentId) implements ShippingReply {
	}

	record Cancelled(Long orderId, Long shipmentId) implements ShippingReply {
	}

	record Failed(Long orderId, String reason) implements ShippingReply {
	}

}
