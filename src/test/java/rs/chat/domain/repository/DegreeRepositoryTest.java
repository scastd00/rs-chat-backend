package rs.chat.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Degree;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class DegreeRepositoryTest {

	@Autowired private DegreeRepository underTest;
	private final Degree degree = new Degree(1L, "Bachelor", emptySet());

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	@Disabled
	void itShouldFindByName() {
		// given
		// when
		this.underTest.save(this.degree);

		// then
//		Degree expected = this.underTest.findByName(this.degree.getName());
//		assertThat(expected).isNotNull();
	}

	@Test
	void itShouldExistByName() {
		// given
		// when
		this.underTest.save(this.degree);

		// then
		boolean expected = this.underTest.existsByName(this.degree.getName());
		assertThat(expected).isTrue();
	}
}
