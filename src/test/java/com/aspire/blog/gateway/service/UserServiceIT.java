package com.aspire.blog.gateway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.transaction.annotation.Transactional;

import com.aspire.blog.gateway.GatewayApp;
import com.aspire.blog.gateway.config.Constants;
import com.aspire.blog.gateway.domain.User;
import com.aspire.blog.gateway.repository.UserRepository;
import com.aspire.blog.gateway.service.dto.UserDTO;
import com.aspire.blog.gateway.service.util.RandomUtil;

/**
 * Integration tests for {@link UserService}.
 */
@EmbeddedKafka
@SpringBootTest(classes = GatewayApp.class)
@Transactional
public class UserServiceIT {

	private static final String DEFAULT_LOGIN = "johndoe";

	private static final String DEFAULT_EMAIL = "johndoe@localhost";

	private static final String DEFAULT_FIRSTNAME = "john";

	private static final String DEFAULT_LASTNAME = "doe";

	private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";

	private static final String DEFAULT_LANGKEY = "dummy";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private AuditingHandler auditingHandler;

	@Mock
	private DateTimeProvider dateTimeProvider;

	private User user;

	@BeforeEach
	public void init() {
		user = new User();
		user.setLogin(DEFAULT_LOGIN);
		user.setPassword(RandomStringUtils.random(60));
		user.setActivated(true);
		user.setEmail(DEFAULT_EMAIL);
		user.setFirstName(DEFAULT_FIRSTNAME);
		user.setLastName(DEFAULT_LASTNAME);
		user.setImageUrl(DEFAULT_IMAGEURL);
		user.setLangKey(DEFAULT_LANGKEY);

		when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));
		auditingHandler.setDateTimeProvider(dateTimeProvider);
	}

	@Test
	@Transactional
	public void assertThatUserMustExistToResetPassword() {
		userRepository.saveAndFlush(user);
		Optional<User> maybeUser = userService.requestPasswordReset("invalid.login@localhost");
		assertThat(maybeUser).isNotPresent();

		maybeUser = userService.requestPasswordReset(user.getEmail());
		assertThat(maybeUser).isPresent();
		assertThat(maybeUser.orElse(null).getEmail()).isEqualTo(user.getEmail());
		assertThat(maybeUser.orElse(null).getResetDate()).isNotNull();
		assertThat(maybeUser.orElse(null).getResetKey()).isNotNull();
	}

	@Test
	@Transactional
	public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
		user.setActivated(false);
		userRepository.saveAndFlush(user);

		Optional<User> maybeUser = userService.requestPasswordReset(user.getLogin());
		assertThat(maybeUser).isNotPresent();
		userRepository.delete(user);
	}

	@Test
	@Transactional
	public void assertThatResetKeyMustNotBeOlderThan24Hours() {
		Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
		String resetKey = RandomUtil.generateResetKey();
		user.setActivated(true);
		user.setResetDate(daysAgo);
		user.setResetKey(resetKey);
		userRepository.saveAndFlush(user);

		Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
		assertThat(maybeUser).isNotPresent();
		userRepository.delete(user);
	}

	@Test
	@Transactional
	public void assertThatResetKeyMustBeValid() {
		Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
		user.setActivated(true);
		user.setResetDate(daysAgo);
		user.setResetKey("1234");
		userRepository.saveAndFlush(user);

		Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
		assertThat(maybeUser).isNotPresent();
		userRepository.delete(user);
	}

	@Test
	@Transactional
	public void assertThatUserCanResetPassword() {
		String oldPassword = user.getPassword();
		Instant daysAgo = Instant.now().minus(2, ChronoUnit.HOURS);
		String resetKey = RandomUtil.generateResetKey();
		user.setActivated(true);
		user.setResetDate(daysAgo);
		user.setResetKey(resetKey);
		userRepository.saveAndFlush(user);

		Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
		assertThat(maybeUser).isPresent();
		assertThat(maybeUser.orElse(null).getResetDate()).isNull();
		assertThat(maybeUser.orElse(null).getResetKey()).isNull();
		assertThat(maybeUser.orElse(null).getPassword()).isNotEqualTo(oldPassword);

		userRepository.delete(user);
	}

	@Test
	@Transactional
	public void assertThatNotActivatedUsersWithNotNullActivationKeyCreatedBefore3DaysAreDeleted() {
		Instant now = Instant.now();
		when(dateTimeProvider.getNow()).thenReturn(Optional.of(now.minus(4, ChronoUnit.DAYS)));
		user.setActivated(false);
		user.setActivationKey(RandomStringUtils.random(20));
		User dbUser = userRepository.saveAndFlush(user);
		dbUser.setCreatedDate(now.minus(4, ChronoUnit.DAYS));
		userRepository.saveAndFlush(user);
		List<User> users = userRepository
				.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(now.minus(3, ChronoUnit.DAYS));
		assertThat(users).isNotEmpty();
		userService.removeNotActivatedUsers();
		users = userRepository
				.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(now.minus(3, ChronoUnit.DAYS));
		assertThat(users).isEmpty();
	}

	@Test
	@Transactional
	public void assertThatNotActivatedUsersWithNullActivationKeyCreatedBefore3DaysAreNotDeleted() {
		Instant now = Instant.now();
		when(dateTimeProvider.getNow()).thenReturn(Optional.of(now.minus(4, ChronoUnit.DAYS)));
		user.setActivated(false);
		User dbUser = userRepository.saveAndFlush(user);
		dbUser.setCreatedDate(now.minus(4, ChronoUnit.DAYS));
		userRepository.saveAndFlush(user);
		List<User> users = userRepository
				.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(now.minus(3, ChronoUnit.DAYS));
		assertThat(users).isEmpty();
		userService.removeNotActivatedUsers();
		Optional<User> maybeDbUser = userRepository.findById(dbUser.getId());
		assertThat(maybeDbUser).contains(dbUser);
	}

	@Test
	@Transactional
	public void assertThatAnonymousUserIsNotGet() {
		user.setLogin(Constants.ANONYMOUS_USER);
		if (!userRepository.findOneByLogin(Constants.ANONYMOUS_USER).isPresent()) {
			userRepository.saveAndFlush(user);
		}
		final PageRequest pageable = PageRequest.of(0, (int) userRepository.count());
		final Page<UserDTO> allManagedUsers = userService.getAllManagedUsers(pageable);
		assertThat(allManagedUsers.getContent().stream()
				.noneMatch(user -> Constants.ANONYMOUS_USER.equals(user.getLogin()))).isTrue();
	}

}
