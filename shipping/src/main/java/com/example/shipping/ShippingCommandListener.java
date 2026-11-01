package com.example.shipping;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Component;

import com.example.contract.shipping.ShippingCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingCommandListener {

	private final ShipmentService shipmentService;

	@KafkaListener(topics = ShippingCommand.TOPIC, batch = "true")
	public void handle(List<ShippingCommand> commands) {
		try {
			shipmentService.handleAll(commands);
		} catch (RuntimeException e) {
			for (int i = 0; i < commands.size(); i++) {
				try {
					shipmentService.handle(commands.get(i));
				} catch (RuntimeException ex) {
					// 장애 격리
					throw new BatchListenerFailedException("배송 명령 실패", ex, i);
				}
			}
		}
	}

}
