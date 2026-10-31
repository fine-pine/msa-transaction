package com.example.shipping;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.contract.shipping.ShippingCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			log.warn("배송 명령 실패. orderId={}, reason={}", command.orderId(), e.getMessage());
			shipmentService.fail(command.orderId(), e.getMessage());
		} // 기타 에러는 Kafka 재시도
	}

}
