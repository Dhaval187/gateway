package com.aspire.blog.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.aspire.blog.gateway.config.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class GatewayKafkaConsumer {

	private final Logger log = LoggerFactory.getLogger(GatewayKafkaConsumer.class);

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@KafkaListener(topics = Constants.TOPIC_ORDER_FAILED, groupId = "group_id")
	public void consume(String message) {
		log.info("Consumed message in {} : {}", Constants.TOPIC_ORDER_FAILED, message);
		JsonParser jsonParser = new JsonParser();
		JsonObject details = jsonParser.parse(message).getAsJsonObject();
		details.addProperty("success", false);
		messagingTemplate.convertAndSend("/topic/user", details.toString());
	}

	@KafkaListener(topics = Constants.TOPIC_ORDER_PLACED_SUCCESS, groupId = "group_id")
	public void consumeOrderPlaced(String message) {
		log.info("Consumed message in {} : {}", Constants.TOPIC_ORDER_PLACED_SUCCESS, message);
		JsonParser jsonParser = new JsonParser();
		JsonObject details = jsonParser.parse(message).getAsJsonObject();
		details.addProperty("success", true);
		messagingTemplate.convertAndSend("/topic/user", details.toString());
	}
}
