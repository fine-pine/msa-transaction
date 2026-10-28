package com.example.shipping;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.contract.shipping.ShippingCommand;
import com.example.contract.shipping.ShippingReply;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShippingCommandListener {

	private final ShipmentService shipmentService;
	private final KafkaTemplate<String, ShippingReply> kafkaTemplate;

	@KafkaListener(topics = ShippingCommand.TOPIC)
	public void handle(ShippingCommand command) {
		ShippingReply reply;
		try {
			reply = switch (command) {
				case ShippingCommand.Create create -> {
					Long shipmentId = shipmentService.create(create.orderId(), create.address());
					yield new ShippingReply.Created(create.orderId(), shipmentId);
				}
				case ShippingCommand.Cancel cancel -> {
					shipmentService.cancel(cancel.shipmentId());
					yield new ShippingReply.Cancelled(cancel.orderId(), cancel.shipmentId());
				}
			};
		} catch (IllegalArgumentException e) {
			reply = new ShippingReply.Failed(command.orderId(), e.getMessage());
		}
		kafkaTemplate.send(ShippingReply.TOPIC, reply);
	}

}
