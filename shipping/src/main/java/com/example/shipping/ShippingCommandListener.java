package com.example.shipping;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.contract.shipping.ShippingCommand;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShippingCommandListener {

	private final ShipmentService shipmentService;

	@KafkaListener(topics = ShippingCommand.TOPIC)
	public void handle(ShippingCommand command) {
		try {
			switch (command) {
				case ShippingCommand.Create create -> shipmentService.create(create.orderId(), create.address());
				case ShippingCommand.Cancel cancel -> shipmentService.cancel(cancel.shipmentId());
			}
		} catch (IllegalArgumentException e) {
			shipmentService.fail(command.orderId(), e.getMessage());
		}
	}

}
