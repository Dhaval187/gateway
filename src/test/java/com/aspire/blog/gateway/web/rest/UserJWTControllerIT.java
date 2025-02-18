package com.aspire.blog.gateway.web.rest;

import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.aspire.blog.gateway.GatewayApp;
import com.aspire.blog.gateway.domain.User;
import com.aspire.blog.gateway.repository.UserRepository;
import com.aspire.blog.gateway.security.jwt.TokenProvider;
import com.aspire.blog.gateway.web.rest.errors.ExceptionTranslator;
import com.aspire.blog.gateway.web.rest.vm.LoginVM;

/**
 * Integration tests for the {@link UserJWTController} REST controller.
 */
@EmbeddedKafka
@SpringBootTest(classes = GatewayApp.class)
public class UserJWTControllerIT {

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private AuthenticationManagerBuilder authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		UserJWTController userJWTController = new UserJWTController(tokenProvider, authenticationManager);
		this.mockMvc = MockMvcBuilders.standaloneSetup(userJWTController).setControllerAdvice(exceptionTranslator)
				.build();
	}

	@Test
	@Transactional
	public void testAuthorize() throws Exception {
		User user = new User();
		user.setLogin("user-jwt-controller");
		user.setEmail("user-jwt-controller@example.com");
		user.setActivated(true);
		user.setPassword(passwordEncoder.encode("test"));

		userRepository.saveAndFlush(user);

		LoginVM login = new LoginVM();
		login.setUsername("user-jwt-controller");
		login.setPassword("test");
		mockMvc.perform(post("/api/authenticate").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(login))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id_token").isString()).andExpect(jsonPath("$.id_token").isNotEmpty())
				.andExpect(header().string("Authorization", not(nullValue())))
				.andExpect(header().string("Authorization", not(isEmptyString())));
	}

	@Test
	@Transactional
	public void testAuthorizeWithRememberMe() throws Exception {
		User user = new User();
		user.setLogin("user-jwt-controller-remember-me");
		user.setEmail("user-jwt-controller-remember-me@example.com");
		user.setActivated(true);
		user.setPassword(passwordEncoder.encode("test"));

		userRepository.saveAndFlush(user);

		LoginVM login = new LoginVM();
		login.setUsername("user-jwt-controller-remember-me");
		login.setPassword("test");
		login.setRememberMe(true);
		mockMvc.perform(post("/api/authenticate").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(login))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id_token").isString()).andExpect(jsonPath("$.id_token").isNotEmpty())
				.andExpect(header().string("Authorization", not(nullValue())))
				.andExpect(header().string("Authorization", not(isEmptyString())));
	}

	@Test
	public void testAuthorizeFails() throws Exception {
		LoginVM login = new LoginVM();
		login.setUsername("wrong-user");
		login.setPassword("wrong password");
		mockMvc.perform(post("/api/authenticate").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(login))).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.id_token").doesNotExist()).andExpect(header().doesNotExist("Authorization"));
	}
}
