package com.aspire.blog.gateway.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.aspire.blog.gateway.GatewayApp;
import com.aspire.blog.gateway.service.GatewayKafkaProducer;

@EmbeddedKafka
@SpringBootTest(classes = GatewayApp.class)
public class GatewayKafkaResourceIT {

	@Autowired
	private GatewayKafkaProducer kafkaProducer;

	private MockMvc restMockMvc;

	@BeforeEach
	public void setup() {
		GatewayKafkaResource kafkaResource = new GatewayKafkaResource(kafkaProducer);

		this.restMockMvc = MockMvcBuilders.standaloneSetup(kafkaResource).build();
	}

	@Test
	public void sendMessageToKafkaTopic() throws Exception {
		restMockMvc.perform(post("/api/gateway-kafka/publish?message=yolo")).andExpect(status().isOk());
	}
}
