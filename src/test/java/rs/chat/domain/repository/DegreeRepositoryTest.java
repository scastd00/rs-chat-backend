package rs.chat.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Degree;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class DegreeRepositoryTest {

	@Autowired private DegreeRepository underTest;
	private final String name = "Bachelor";
	private final Degree degree = new Degree(1L, name);

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	void itShouldFindByName() {
		// given
		// when
		this.underTest.save(this.degree);

		// then
		Degree expected = this.underTest.findByName(this.name);
		assertThat(expected).isNotNull();
	}

	@Test
	void degreeDoesNotExist() {
		// given
		// when
		this.underTest.save(this.degree);

		// then
		Degree expected = this.underTest.findByName("Master");
		assertThat(expected).isNull();
	}
}
