package rs.chat.utils;

import lombok.NoArgsConstructor;
import rs.chat.Constants;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.BadgeRepository;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.EmojiRepository;
import rs.chat.domain.repository.GroupRepository;
import rs.chat.domain.repository.SessionRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.factories.DefaultFactory;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public final class SaveDefaultsToDB {
	public static Map<String, Session> saveDefaults(
			UserRepository userRepository, GroupRepository groupRepository,
			ChatRepository chatRepository, EmojiRepository emojiRepository,
			BadgeRepository badgeRepository, SessionRepository sessionRepository) {
		saveGroups(groupRepository);
		saveChats(chatRepository);
		saveEmojis(emojiRepository);
		saveBadges(badgeRepository);
		return saveUsersAndSessions(userRepository, sessionRepository);
	}

	private static void saveGroups(GroupRepository groupRepository) {
		if (groupRepository == null) {
			return;
		}

		Group global = DefaultFactory.INSTANCE.createGroup(null, "Global");
		Group group1 = DefaultFactory.INSTANCE.createGroup(null, "Group 1");
		Group group2 = DefaultFactory.INSTANCE.createGroup(null, "Group 2");

		List<Group> groups = List.of(global, group1, group2);
		groupRepository.saveAll(groups);
	}

	private static void saveChats(ChatRepository chatRepository) {
		if (chatRepository == null) {
			return;
		}

		Chat global = DefaultFactory.INSTANCE.createChat(null, "Global", "group");
		Chat chat1 = DefaultFactory.INSTANCE.createChat(null, "Chat 1", "group");
		Chat chat2 = DefaultFactory.INSTANCE.createChat(null, "Chat 2", "group");

		List<Chat> chats = List.of(global, chat1, chat2);
		chatRepository.saveAll(chats);
	}

	private static void saveEmojis(EmojiRepository emojiRepository) {
		if (emojiRepository == null) {
			return;
		}
	}

	private static void saveBadges(BadgeRepository badgeRepository) {
		if (badgeRepository == null) {
			return;
		}
	}

	private static Map<String, Session> saveUsersAndSessions(UserRepository userRepository, SessionRepository sessionRepository) {
		if (sessionRepository == null) {
			return null;
		}

		User student = userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE));
		Session studentSession = sessionRepository.save(DefaultFactory.INSTANCE.createSession(null, student));

		User teacher = userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.TEACHER_ROLE));
		Session teacherSession = sessionRepository.save(DefaultFactory.INSTANCE.createSession(null, teacher));

		User admin = userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.ADMIN_ROLE));
		Session adminSession = sessionRepository.save(DefaultFactory.INSTANCE.createSession(null, admin));

		// Create more sessions to test the logout functionality (and others)
		sessionRepository.save(DefaultFactory.INSTANCE.createSession(null, student));
		sessionRepository.save(DefaultFactory.INSTANCE.createSession(null, teacher));
		sessionRepository.save(DefaultFactory.INSTANCE.createSession(null, admin));

		return Map.of(
				Constants.STUDENT_ROLE, studentSession,
				Constants.TEACHER_ROLE, teacherSession,
				Constants.ADMIN_ROLE, adminSession
		);
	}
}
