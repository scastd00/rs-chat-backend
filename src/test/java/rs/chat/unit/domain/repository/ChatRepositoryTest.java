package rs.chat.unit.domain.repository;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.repository.ChatRepository;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class ChatRepositoryTest {

	@Autowired
	private ChatRepository underTest;
	private Chat chat;

	@BeforeEach
	void setUp() {
		this.chat = new Chat(
				null, "name", "group", "folder",
				new JsonObject(), "25wcv9A", "group-1",
				Collections.emptySet()
		);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	void itShouldFindByName() {
		// given
		this.underTest.save(this.chat);

		// when
		Optional<Chat> expected = this.underTest.findByName(this.chat.getName());

		// then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.chat);
	}

	@Test
	void itShouldNotFindByName() {
		// given
		// when
		Optional<Chat> expected = this.underTest.findByName(this.chat.getName());

		// then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldFindByInvitationCode() {
		// given
		this.underTest.save(this.chat);

		// when
		Optional<Chat> expected = this.underTest.findByInvitationCode(this.chat.getInvitationCode());

		// then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.chat);
	}

	@Test
	void itShouldNotFindByInvitationCode() {
		// given
		// when
		Optional<Chat> expected = this.underTest.findByInvitationCode(this.chat.getInvitationCode());

		// then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldFindByKey() {
		// given
		this.underTest.save(this.chat);

		// when
		Optional<Chat> expected = this.underTest.findByKey(this.chat.getKey());

		// then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.chat);
	}

	@Test
	void itShouldNotFindByKey() {
		// given
		// when
		Optional<Chat> expected = this.underTest.findByKey(this.chat.getKey());

		// then
		assertThat(expected).isNotPresent();
	}
}
