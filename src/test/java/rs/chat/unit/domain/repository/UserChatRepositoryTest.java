package rs.chat.unit.domain.repository;

import com.google.gson.JsonObject;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserChat;
import rs.chat.domain.entity.UserChatId;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.UserChatRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.Constants;
import rs.chat.utils.factories.DefaultFactory;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class UserChatRepositoryTest {
	@Autowired private UserChatRepository underTest;
	@Autowired private UserRepository userRepository;
	@Autowired private ChatRepository chatRepository;
	private User user1;
	private User user2;
	private Chat chat;
	private UserChat userChat1;
	private UserChat userChat2;
	private UserChatId userChatId1;
	private UserChatId userChatId2;

	@BeforeEach
	void setUp() {
		this.user1 = this.userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE));

		this.user2 = this.userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE));

		this.chat = this.chatRepository.save(new Chat(
				null, "name", "group", "folder",
				new JsonObject(), "25wcv9A", "group-1",
				Collections.emptySet()
		));

		this.userChatId1 = new UserChatId(this.user1.getId(), this.chat.getId());
		this.userChatId2 = new UserChatId(this.user2.getId(), this.chat.getId());
		this.userChat1 = new UserChat(this.userChatId1, this.user1, this.chat);
		this.userChat2 = new UserChat(this.userChatId2, this.user2, this.chat);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.userRepository.deleteAll();
		this.chatRepository.deleteAll();
	}

	@Test
	void itShouldFindAllById_UserId() {
		// Given
		this.underTest.save(this.userChat1);

		// When
		List<UserChat> expected = this.underTest.findAllById_UserId(this.user1.getId());

		// Then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat1);
	}

	@Test
	void itShouldNotFindAllById_UserId() {
		// Given
		// When
		List<UserChat> expected = this.underTest.findAllById_UserId(this.user1.getId());

		// Then
		assertThat(expected).asList().isEmpty();
	}

	@Test
	void itShouldExistsById_UserIdAndId_ChatId() {
		// Given
		this.underTest.save(this.userChat1);

		// When
		boolean expected = this.underTest.existsById_UserIdAndId_ChatId(this.user1.getId(), this.chat.getId());

		// Then
		assertThat(expected).isTrue();
	}

	@Test
	void itShouldNotExistsById_UserIdAndId_ChatId() {
		// Given
		// When
		boolean expected = this.underTest.existsById_UserIdAndId_ChatId(this.user1.getId(), this.chat.getId());

		// Then
		assertThat(expected).isFalse();
	}

	@Test
	void itShouldFindAllById_ChatId() {
		// Given
		this.underTest.save(this.userChat1);

		// When
		List<UserChat> expected = this.underTest.findAllById_ChatId(this.chat.getId());

		// Then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat1);
	}

	@Test
	void itShouldFindAllById_ChatIdMulti() {
		// Given
		this.underTest.save(this.userChat1);
		this.underTest.save(this.userChat2);

		// When
		List<UserChat> expected = this.underTest.findAllById_ChatId(this.chat.getId());

		// Then
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(expected)
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat2);
	}

	@Test
	void itShouldNotFindAllById_ChatId() {
		// Given
		// When
		List<UserChat> expected = this.underTest.findAllById_ChatId(this.chat.getId());

		// Then
		assertThat(expected).asList().isEmpty();
	}

	@Test
	void itShouldDeleteAllById_ChatId() {
		// Given
		this.underTest.save(this.userChat1);
		this.underTest.save(this.userChat2);

		// When
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert = assertThat(this.underTest.findAll())
				.asList()
				.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat2);

		this.underTest.deleteAllById_ChatId(this.chat.getId());

		// Then
		assertThat(this.underTest.findAll()).asList().isEmpty();
	}

	@Test
	void itShouldNotDeleteAllById_ChatId() {
		// Given
		// When
		this.underTest.deleteAllById_ChatId(this.chat.getId());

		// Then
		assertThat(this.underTest.findAll()).asList().isEmpty();
	}

	@Test
	void itShouldDeleteById_UserIdAndId_ChatId() {
		// Given
		this.underTest.save(this.userChat1);
		this.underTest.save(this.userChat2);

		// When
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(this.underTest.findAll())
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat2);

		this.underTest.deleteById_UserIdAndId_ChatId(this.user1.getId(), this.chat.getId());

		// Then
		assertThat(this.underTest.findAll())
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userChat2);
	}

	@Test
	void itShouldNotDeleteById_UserIdAndId_ChatId() {
		// Given
		// When
		this.underTest.deleteById_UserIdAndId_ChatId(this.user1.getId(), this.chat.getId());

		// Then
		assertThat(this.underTest.findAll()).asList().isEmpty();
	}
}
