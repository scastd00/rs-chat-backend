package rs.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.DegreeRepository;
import rs.chat.domain.repository.UserChatRepository;
import rs.chat.exceptions.BadRequestException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataJpaTest
class DegreeServiceTest {

	@Mock private DegreeRepository degreeRepository;
	@Mock private ChatRepository chatRepository;
	@Mock private UserChatRepository userChatRepository;
	private DegreeService underTest;
	private Degree degree;

	@BeforeEach
	void setUp() {
		this.underTest = new DegreeService(this.degreeRepository, this.chatRepository, this.userChatRepository);
		this.degree = new Degree(1L, "Test");
	}

	@Test
	void testGetDegrees() {
		// given
		// when
		this.underTest.getDegrees();

		// then
		verify(this.degreeRepository).findAll(Sort.by("name"));
	}

	@Test
	void testGetByName() {
		// given
		// when
		this.underTest.getByName(this.degree.getName());

		// then
		verify(this.degreeRepository).findByName(this.degree.getName());
	}

	@Test
	void testSaveDegreeOk() {
		// given
		// when
		this.underTest.saveDegree(this.degree);

		// then
		ArgumentCaptor<Degree> degreeArgumentCaptor = ArgumentCaptor.forClass(Degree.class);
		verify(this.degreeRepository).save(degreeArgumentCaptor.capture());
		Degree capturedDegree = degreeArgumentCaptor.getValue();
		assertEquals(this.degree.getName(), capturedDegree.getName());
	}

	@Test
	void testSaveExistingDegreeThrowsException() {
		// given
		given(this.degreeRepository.existsByName(this.degree.getName())).willReturn(true);

		// when
		// then
		assertThatThrownBy(() -> this.underTest.saveDegree(this.degree))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Degree already exists: " + this.degree.getName());
	}

	@Test
	void testExistsDegree() {
		// given
		// when
		this.underTest.existsDegree(this.degree.getName());

		// then
		verify(this.degreeRepository).existsByName(this.degree.getName());
	}

	@Test
	void testChangeDegreeNameOkWithUpdateInDB() {
		// given
		given(this.degreeRepository.findByName(this.degree.getName())).willReturn(this.degree);

		// when
		this.underTest.changeDegreeName(this.degree.getName(), "New name");

		// then
		verify(this.degreeRepository).save(this.degree);
		assertThat(this.degree.getName()).isEqualTo("New name");
	}

	@Test
	void testChangeDegreeNameNoDegree() {
		// given
		given(this.degreeRepository.findByName(this.degree.getName())).willReturn(null);

		// when
		// then
		assertThatThrownBy(() -> this.underTest.changeDegreeName(this.degree.getName(), "New name"))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Degree does not exist: " + this.degree.getName());
	}

	@Test
	void testChangeDegreeNameEqualNames() {
		// given
		given(this.degreeRepository.findByName(this.degree.getName())).willReturn(this.degree);

		// when
		Degree result = this.underTest.changeDegreeName(this.degree.getName(), this.degree.getName());

		// then
		assertThat(result).isSameAs(this.degree); // Same memory reference, since the object is returned without changes
	}

//	@Test
//	void testDeleteDegreeByName() {
//		// given
//		given(this.degreeRepository.existsByName(this.degree.getName())).willReturn(true);
//
//		// when
//		this.underTest.deleteDegreeByName(this.degree.getName());
//
//		// then
//		verify(this.degreeRepository).deleteByName(this.degree.getName());
//		verify(this.chatRepository).deleteByName(this.degree.getName());
//	}
//
//	@Test
//	void testDeleteDegreeByNameNoDegree() {
//		// given
//		given(this.degreeRepository.existsByName(this.degree.getName())).willReturn(false);
//
//		// when
//		// then
//		assertThatThrownBy(() -> this.underTest.deleteDegreeByName(this.degree.getName()))
//				.isInstanceOf(NotFoundException.class)
//				.hasMessageContaining("'%s' does not exist".formatted(this.degree.getName()));
//	}
}
