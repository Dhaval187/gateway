package com.aspire.blog.gateway.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.aspire.blog.gateway.GatewayApp;
import com.aspire.blog.gateway.domain.User;
import com.aspire.blog.gateway.service.dto.UserDTO;

/**
 * Integration tests for {@link UserMapper}.
 */
@EmbeddedKafka
@SpringBootTest(classes = GatewayApp.class)
public class UserMapperIT {

	private static final String DEFAULT_LOGIN = "johndoe";
	private static final Long DEFAULT_ID = 1L;

	@Autowired
	private UserMapper userMapper;

	private User user;
	private UserDTO userDto;

	@BeforeEach
	public void init() {
		user = new User();
		user.setLogin(DEFAULT_LOGIN);
		user.setPassword(RandomStringUtils.random(60));
		user.setActivated(true);
		user.setEmail("johndoe@localhost");
		user.setFirstName("john");
		user.setLastName("doe");
		user.setImageUrl("image_url");
		user.setLangKey("en");

		userDto = new UserDTO(user);
	}

	@Test
	public void usersToUserDTOsShouldMapOnlyNonNullUsers() {
		List<User> users = new ArrayList<>();
		users.add(user);
		users.add(null);

		List<UserDTO> userDTOS = userMapper.usersToUserDTOs(users);

		assertThat(userDTOS).isNotEmpty();
		assertThat(userDTOS).size().isEqualTo(1);
	}

	@Test
	public void userDTOsToUsersShouldMapOnlyNonNullUsers() {
		List<UserDTO> usersDto = new ArrayList<>();
		usersDto.add(userDto);
		usersDto.add(null);

		List<User> users = userMapper.userDTOsToUsers(usersDto);

		assertThat(users).isNotEmpty();
		assertThat(users).size().isEqualTo(1);
	}

	@Test
	public void userDTOsToUsersWithAuthoritiesStringShouldMapToUsersWithAuthoritiesDomain() {
		Set<String> authoritiesAsString = new HashSet<>();
		authoritiesAsString.add("ADMIN");
		userDto.setAuthorities(authoritiesAsString);

		List<UserDTO> usersDto = new ArrayList<>();
		usersDto.add(userDto);

		List<User> users = userMapper.userDTOsToUsers(usersDto);

		assertThat(users).isNotEmpty();
		assertThat(users).size().isEqualTo(1);
		assertThat(users.get(0).getAuthorities()).isNotNull();
		assertThat(users.get(0).getAuthorities()).isNotEmpty();
		assertThat(users.get(0).getAuthorities().iterator().next().getName()).isEqualTo("ADMIN");
	}

	@Test
	public void userDTOsToUsersMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
		userDto.setAuthorities(null);

		List<UserDTO> usersDto = new ArrayList<>();
		usersDto.add(userDto);

		List<User> users = userMapper.userDTOsToUsers(usersDto);

		assertThat(users).isNotEmpty();
		assertThat(users).size().isEqualTo(1);
		assertThat(users.get(0).getAuthorities()).isNotNull();
		assertThat(users.get(0).getAuthorities()).isEmpty();
	}

	@Test
	public void userDTOToUserMapWithAuthoritiesStringShouldReturnUserWithAuthorities() {
		Set<String> authoritiesAsString = new HashSet<>();
		authoritiesAsString.add("ADMIN");
		userDto.setAuthorities(authoritiesAsString);

		User user = userMapper.userDTOToUser(userDto);

		assertThat(user).isNotNull();
		assertThat(user.getAuthorities()).isNotNull();
		assertThat(user.getAuthorities()).isNotEmpty();
		assertThat(user.getAuthorities().iterator().next().getName()).isEqualTo("ADMIN");
	}

	@Test
	public void userDTOToUserMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
		userDto.setAuthorities(null);

		User user = userMapper.userDTOToUser(userDto);

		assertThat(user).isNotNull();
		assertThat(user.getAuthorities()).isNotNull();
		assertThat(user.getAuthorities()).isEmpty();
	}

	@Test
	public void userDTOToUserMapWithNullUserShouldReturnNull() {
		assertThat(userMapper.userDTOToUser(null)).isNull();
	}

	@Test
	public void testUserFromId() {
		assertThat(userMapper.userFromId(DEFAULT_ID).getId()).isEqualTo(DEFAULT_ID);
		assertThat(userMapper.userFromId(null)).isNull();
	}
}
