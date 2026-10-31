package com.example.contract.shipping;

public sealed interface ShippingReply {

	String TOPIC = "shipping.reply";

	Long orderId();

	record Created(Long orderId, Long shipmentId) implements ShippingReply {
		public static final String TYPE = "CREATED";
	}

	record Cancelled(Long orderId, Long shipmentId) implements ShippingReply {
		public static final String TYPE = "CANCELLED";
	}

	record Failed(Long orderId, String reason) implements ShippingReply {
		public static final String TYPE = "FAILED";
	}

}
