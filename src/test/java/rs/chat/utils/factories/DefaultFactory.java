package rs.chat.utils.factories;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.config.security.JWTService;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.entity.Emoji;
import rs.chat.domain.entity.File;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.dtos.BadgeDto;
import rs.chat.domain.entity.dtos.ChatDto;
import rs.chat.domain.entity.dtos.DegreeDto;
import rs.chat.domain.entity.dtos.EmojiDto;
import rs.chat.domain.entity.dtos.FileDto;
import rs.chat.domain.entity.dtos.GroupDto;
import rs.chat.domain.entity.dtos.SessionDto;
import rs.chat.domain.entity.dtos.SubjectDto;
import rs.chat.domain.entity.dtos.UserDto;
import rs.chat.domain.entity.mappers.BadgeMapper;
import rs.chat.domain.entity.mappers.BadgeMapperImpl;
import rs.chat.domain.entity.mappers.ChatMapper;
import rs.chat.domain.entity.mappers.ChatMapperImpl;
import rs.chat.domain.entity.mappers.DegreeMapper;
import rs.chat.domain.entity.mappers.DegreeMapperImpl;
import rs.chat.domain.entity.mappers.EmojiMapper;
import rs.chat.domain.entity.mappers.EmojiMapperImpl;
import rs.chat.domain.entity.mappers.FileMapper;
import rs.chat.domain.entity.mappers.FileMapperImpl;
import rs.chat.domain.entity.mappers.GroupMapper;
import rs.chat.domain.entity.mappers.GroupMapperImpl;
import rs.chat.domain.entity.mappers.SessionMapper;
import rs.chat.domain.entity.mappers.SessionMapperImpl;
import rs.chat.domain.entity.mappers.SubjectMapper;
import rs.chat.domain.entity.mappers.SubjectMapperImpl;
import rs.chat.domain.entity.mappers.UserMapper;
import rs.chat.domain.entity.mappers.UserMapperImpl;

import java.time.Instant;
import java.util.Collections;

import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.utils.TestConstants.FAKER;
import static rs.chat.utils.TestConstants.TEST_PASSWORD;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultFactory {
	public static final DefaultFactory INSTANCE = new DefaultFactory();

	private final BadgeMapper badgeMapper = new BadgeMapperImpl();
	private final ChatMapper chatMapper = new ChatMapperImpl();
	private final DegreeMapper degreeMapper = new DegreeMapperImpl();
	private final EmojiMapper emojiMapper = new EmojiMapperImpl();
	private final FileMapper fileMapper = new FileMapperImpl();
	private final GroupMapper groupMapper = new GroupMapperImpl();
	private final SessionMapper sessionMapper = new SessionMapperImpl();
	private final SubjectMapper subjectMapper = new SubjectMapperImpl();
	private final UserMapper userMapper = new UserMapperImpl();

	public Badge createBadge(Long id, String title, String description, int points) {
		return new Badge(
				id, title, description,
				"/images/badges/%s.png".formatted(title),
				TEXT_MESSAGE.type(), points, emptySet()
		);
	}

	public BadgeDto createBadgeDto(Long id, String title, String description, int points) {
		return badgeMapper.toDto(createBadge(id, title, description, points));
	}

	public Chat createChat(Long id, String name, String type) {
		return new Chat(
				id, name, type, "%s/%s".formatted(type, name),
				new JsonObject(), randomAlphanumeric(7), "%s-%d".formatted(type, id),
				Collections.emptySet()
		);
	}

	public ChatDto createChatDto(Long id, String name, String type) {
		return chatMapper.toDto(createChat(id, name, type));
	}

	public Degree createDegree(Long id, String name) {
		return new Degree(id, name, emptySet());
	}

	public DegreeDto createDegreeDto(Long id, String name) {
		return degreeMapper.toDto(createDegree(id, name));
	}

	public Emoji createEmoji(Long id) {
		return null;
	}

	public EmojiDto createEmojiDto(Long id) {
		return emojiMapper.toDto(createEmoji(id));
	}

	public File createFile(Long id) {
		return null;
	}

	public FileDto createFileDto(Long id) {
		return fileMapper.toDto(createFile(id));
	}

	public Group createGroup(Long id, String name) {
		return new Group(id, name, Collections.emptySet());
	}

	public GroupDto createGroupDto(Long id, String name) {
		return groupMapper.toDto(createGroup(id, name));
	}

	public Session createSession(Long id, User user) {
		return new Session(
				id, FAKER.internet().ipV4Address(), Instant.now(), Instant.now().plusSeconds(60),
				JWTService.generateTmpToken(user.getUsername(), user.getRole()), user
		);
	}

	public SessionDto createSessionDto(Long id, User user) {
		return sessionMapper.toDto(createSession(id, user));
	}

	public Subject createSubject(Long id, String name, String period, String type, Degree degree) {
		byte credits = (byte) FAKER.number().numberBetween(3, 6);
		byte grade = (byte) FAKER.number().numberBetween(1, 4);

		return new Subject(id, name, period, type, credits, grade, degree, emptySet(), emptySet());
	}

	public SubjectDto createSubjectDto(Long id, String name, String period, String type, Degree degree) {
		return subjectMapper.toDto(createSubject(id, name, period, type, degree));
	}

	public User createUser(Long id, String role) {
		String name = FAKER.name().username();
		String fullName = name.replace(".", "");
		String username = name.split("\\.")[0] + randomAlphanumeric(4);
		String email = username + "@hello.com";
		String code = randomAlphanumeric(6);
		byte age = (byte) FAKER.number().numberBetween(18, 100);

		return new User(
				id, username, TEST_PASSWORD, email,
				fullName, age, null, role,
				null, code, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		);
	}

	public UserDto createUserDto(Long id, String role) {
		return userMapper.toDto(createUser(id, role));
	}
}
