package rs.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.BadRequestException;
import rs.chat.utils.Constants;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	// We mock it because we already know that it works perfectly
	@Mock private UserRepository userRepository;
	@Mock private PasswordEncoder passwordEncoder;
	private UserService underTest;
	private User user;

	@BeforeEach
	void setUp() {
		this.underTest = new UserService(this.userRepository, this.passwordEncoder);
		this.user = new User(
				1L,
				"david",
				"12345",
				"david@hello.com",
				"David Gar Dom",
				(byte) 21,
				null,
				Constants.STUDENT_ROLE,
				null,
				null
		);
	}

	@Test
	@Disabled
	void loadUserByUsername() {
	}

	@Test
	void getUsers() {
		// given
		// when
		this.underTest.getUsers();

		// then
		verify(this.userRepository).findAll();
	}

	@Test
	void canCreateUser() {
		// given
		// when
		this.underTest.createUser(this.user);

		// then

		// We capture the user that was used as parameter in the repository method
		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		verify(this.userRepository).save(userArgumentCaptor.capture());
		User capturedUser = userArgumentCaptor.getValue();
		assertThat(capturedUser).isEqualTo(this.user);
	}

	@Test
	void willThrowWhenEmailIsTaken() {
		// given
		given(this.userRepository.existsByEmail(user.getEmail())).willReturn(true);

		// when
		// then
		assertThatThrownBy(() -> this.underTest.createUser(user))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Email %s taken".formatted(this.user.getEmail()));
		verify(this.userRepository, never()).save(any());
	}

	@Test
	@Disabled
	void deleteUser() {
		// given
		// when
		this.underTest.deleteUser(this.user.getId());

		// then
		verify(this.userRepository).deleteById(this.user.getId());
	}

	@Test
	@Disabled
	void getUser() {
	}

	@Test
	@Disabled
	void setRoleToUser() {
	}

	@Test
	@Disabled
	void getUserByEmail() {
	}

	@Test
	@Disabled
	void getUserByCode() {
	}
}
