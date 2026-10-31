package com.example.shipping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class OutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final JsonMapper jsonMapper;

	@Transactional
	public void append(String topic, Long orderId, String type, Object payload) {
		String json = jsonMapper.writeValueAsString(payload);
		OutboxEvent event = new OutboxEvent(topic, orderId, type, json);
		outboxEventRepository.save(event);
	}

}
