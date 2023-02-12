package rs.chat.domain.repository;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Chat;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.RECURSIVE_COMPARISON_CONFIGURATION;

@DataJpaTest
class ChatRepositoryTest {

	@Autowired
	private ChatRepository underTest;
	private Chat chat;

	@BeforeEach
	void setUp() {
		this.chat = new Chat(
				1L, "name", "group", "folder",
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
				.usingRecursiveComparison(RECURSIVE_COMPARISON_CONFIGURATION)
				.isEqualTo(this.chat);
	}

	@Test
	void itShouldNotFindByName() {
		// given
		this.underTest.save(this.chat);

		// when
		Optional<Chat> expected = this.underTest.findByName("not-found");

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
				.usingRecursiveComparison(RECURSIVE_COMPARISON_CONFIGURATION)
				.isEqualTo(this.chat);
	}

	@Test
	void itShouldNotFindByInvitationCode() {
		// given
		this.underTest.save(this.chat);

		// when
		Optional<Chat> expected = this.underTest.findByInvitationCode("not-found");

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
				.usingRecursiveComparison(RECURSIVE_COMPARISON_CONFIGURATION)
				.isEqualTo(this.chat);
	}

	@Test
	void itShouldNotFindByKey() {
		// given
		this.underTest.save(this.chat);

		// when
		Optional<Chat> expected = this.underTest.findByKey("not-found");

		// then
		assertThat(expected).isNotPresent();
	}
}
