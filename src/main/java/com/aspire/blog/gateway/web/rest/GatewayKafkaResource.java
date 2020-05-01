package com.aspire.blog.gateway.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.blog.gateway.service.GatewayKafkaProducer;

@RestController
@RequestMapping(value = "/api/gateway-kafka")
public class GatewayKafkaResource {

	private final Logger log = LoggerFactory.getLogger(GatewayKafkaResource.class);

	private GatewayKafkaProducer kafkaProducer;

	public GatewayKafkaResource(GatewayKafkaProducer kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}

	@PostMapping(value = "/publish")
	public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
		log.debug("REST request to send to Kafka topic the message : {}", message);
		this.kafkaProducer.sendMessage(message);
	}
}
