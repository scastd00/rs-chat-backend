package rs.chat.unit.domain.repository;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserGroup;
import rs.chat.domain.entity.UserGroupId;
import rs.chat.domain.repository.GroupRepository;
import rs.chat.domain.repository.UserGroupRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.Constants;

import java.util.List;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;
import static rs.chat.TestUtils.createUserWithRole;

@DataJpaTest
class UserGroupRepositoryTest {
	@Autowired private UserGroupRepository underTest;
	@Autowired private UserRepository userRepository;
	@Autowired private GroupRepository groupRepository;
	private Group group;
	private User user1;
	private User user2;
	private UserGroup userGroup1;
	private UserGroup userGroup2;
	private UserGroupId userGroupId1;
	private UserGroupId userGroupId2;

	@BeforeEach
	void setUp() {
		this.user1 = this.userRepository.save(createUserWithRole(Constants.STUDENT_ROLE));

		this.user2 = this.userRepository.save(createUserWithRole(Constants.STUDENT_ROLE));

		this.group = this.groupRepository.save(new Group(null, "group-1", emptySet()));

		this.userGroupId1 = new UserGroupId(this.user1.getId(), this.group.getId());
		this.userGroupId2 = new UserGroupId(this.user2.getId(), this.group.getId());
		this.userGroup1 = new UserGroup(this.userGroupId1, this.user1, this.group);
		this.userGroup2 = new UserGroup(this.userGroupId2, this.user2, this.group);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.userRepository.deleteAll();
		this.groupRepository.deleteAll();
	}

	@Test
	void itShouldDeleteAllById_GroupId() {
		// Given
		this.underTest.save(this.userGroup1);
		this.underTest.save(this.userGroup2);

		// When
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(this.underTest.findAll())
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userGroup1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userGroup2);

		this.underTest.deleteAllById_GroupId(this.group.getId());

		// Then
		assertThat(this.underTest.findAll()).asList().isEmpty();
	}

	@Test
	void itShouldNotDeleteAllById_GroupId() {
		// Given
		this.underTest.save(this.userGroup1);
		this.underTest.save(this.userGroup2);

		// When
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(this.underTest.findAll())
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userGroup1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userGroup2);

		this.underTest.deleteAllById_GroupId(this.group.getId() + 1);

		// Then
		listAssert = assertThat(this.underTest.findAll())
				.asList()
				.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userGroup1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userGroup2);
	}
}
