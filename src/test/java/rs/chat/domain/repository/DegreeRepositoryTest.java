package rs.chat.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Degree;

import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.RECURSIVE_COMPARISON_CONFIGURATION;

@DataJpaTest
class DegreeRepositoryTest {

	@Autowired
	private DegreeRepository underTest;
	private final String name = "Bachelor";
	private Degree degree;

	@BeforeEach
	void setUp() {
		this.degree = new Degree(1L, this.name, emptySet());
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
		                    .usingRecursiveComparison(RECURSIVE_COMPARISON_CONFIGURATION)
		                    .isEqualTo(this.degree);
	}

	@Test
	void itShouldNotFindByName() {
		// given
		this.underTest.save(this.degree);

		// when
		Optional<Degree> expected = this.underTest.findByName("Master");

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
		this.underTest.save(this.degree);

		// when
		boolean expected = this.underTest.existsByName("Master");

		// then
		assertThat(expected).isFalse();
	}
}
