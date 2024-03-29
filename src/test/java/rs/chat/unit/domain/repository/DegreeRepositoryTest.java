package rs.chat.unit.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.repository.DegreeRepository;

import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class DegreeRepositoryTest {

	@Autowired
	private DegreeRepository underTest;
	private final String name = "Bachelor";
	private Degree degree;

	@BeforeEach
	void setUp() {
		this.degree = new Degree(null, this.name, emptySet());
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	void itShouldFindByName() {
		// given
		this.underTest.save(this.degree);

		// when
		Optional<Degree> expected = this.underTest.findByName(this.name);

		// then
		assertThat(expected).isPresent()
		                    .get()
		                    .usingRecursiveComparison(TEST_COMPARISON_CONFIG)
		                    .isEqualTo(this.degree);
	}

	@Test
	void itShouldNotFindByName() {
		// given
		// when
		Optional<Degree> expected = this.underTest.findByName(this.name);

		// then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldExistByName() {
		// given
		this.underTest.save(this.degree);

		// when
		boolean expected = this.underTest.existsByName(this.name);

		// then
		assertThat(expected).isTrue();
	}

	@Test
	void itShouldNotExistByName() {
		// given
		// when
		boolean expected = this.underTest.existsByName(this.name);

		// then
		assertThat(expected).isFalse();
	}
}
