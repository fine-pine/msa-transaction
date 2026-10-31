package com.example.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String topic;

	@Column(nullable = false)
	private Long orderId;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private String payload;

	public OutboxEvent(String topic, Long orderId, String type, String payload) {
		this.topic = topic;
		this.orderId = orderId;
		this.type = type;
		this.payload = payload;
	}

}
