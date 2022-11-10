package rs.chat.domain.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserChat;
import rs.chat.domain.entity.UserChatPK;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.UserChatRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
	private final ChatRepository chatRepository;
	private final UserChatRepository userChatRepository;
	private final UserRepository userRepository;

	/**
	 * @return list of all chats stored in database.
	 */
	public List<Chat> getAllChats() {
		return this.chatRepository.findAll();
	}

	/**
	 * Finds a chat by id and returns it.
	 *
	 * @param id id of the chat to be found.
	 *
	 * @return found chat.
	 *
	 * @throws BadRequestException if chat with given id does not exist.
	 */
	public Chat getChatById(Long id) {
		return this.chatRepository.findById(id).orElseThrow(() -> {
			throw new BadRequestException("Chat with id=%d does not exist".formatted(id));
		});
	}

	/**
	 * Chats are created when a degree/course/subject/user is created.
	 *
	 * @param chat the new chat to save.
	 *
	 * @return the created chat.
	 */
	public Chat saveChat(Chat chat) {
		return this.chatRepository.save(chat);
	}

	/**
	 * Retrieves all the chats to which the user can access.
	 *
	 * @param userId id of the user.
	 *
	 * @return list of chats to which the user can access.
	 */
	public List<Chat> getAllChatsOfUser(Long userId) {
		List<Chat> chatsOfUser = new ArrayList<>();

		this.userChatRepository.findAllByUserChatPK_UserId(userId)
		                       .stream()
		                       .map(UserChat::getUserChatPK)
		                       .map(userChatPK -> this.chatRepository.findById(userChatPK.getChatId()))
		                       .forEach(chat -> chat.ifPresent(chatsOfUser::add));

		return chatsOfUser;
	}

	/**
	 * Retrieves all the chats to which the user can access grouped by type.
	 *
	 * @param userId id of the user.
	 *
	 * @return map of chats to which the user can access grouped by type.
	 */
	public Map<String, List<Map<String, Object>>> getAllChatsOfUserGroupedByType(Long userId) {
		List<Chat> allChatsOfUser = this.getAllChatsOfUser(userId);
		Map<String, List<Map<String, Object>>> groups = new HashMap<>();

		allChatsOfUser.forEach(chat -> {
			String chatType = chat.getType();
			Map<String, Object> chatItem = new HashMap<>();
			chatItem.put("id", chat.getId());
			chatItem.put("name", chat.getName());

			if (!groups.containsKey(chatType)) {
				groups.put(chatType, Lists.newArrayList(chatItem));
			} else {
				groups.get(chatType).add(chatItem);
			}
		});

		return groups;
	}

	/**
	 * Retrieves a chat given its name.
	 *
	 * @param chatName name of the chat.
	 *
	 * @return found chat.
	 */
	public Chat getByName(String chatName) {
		return this.chatRepository.findByName(chatName)
		                          .orElseThrow(() -> new NotFoundException("Chat with name=%s does not exist".formatted(chatName)));
	}

	/**
	 * Adds a user to a chat.
	 *
	 * @param userId id of the user.
	 * @param chatId id of the chat.
	 */
	public void addUserToChat(Long userId, Long chatId) {
		this.userChatRepository.save(new UserChat(new UserChatPK(userId, chatId)));
	}

	/**
	 * Retrieves a chat given its joining code.
	 *
	 * @param code joining code of the chat.
	 *
	 * @return found chat.
	 */
	public Chat getChatByCode(String code) {
		return this.chatRepository.findByInvitationCode(code)
		                          .orElseThrow(() -> new BadRequestException("Invalid chat code: " + code));
	}

	/**
	 * Checks if a user is a member of a chat.
	 *
	 * @param userId id of the user.
	 * @param chatId id of the chat.
	 *
	 * @return true if the user is a member of the chat, false otherwise.
	 */
	public boolean userAlreadyBelongsToChat(Long userId, Long chatId) {
		return this.userChatRepository.existsByUserChatPK_UserIdAndUserChatPK_ChatId(userId, chatId);
	}

	/**
	 * Determines if a user can access a chat.
	 *
	 * @param userId id of the user.
	 * @param chatId id of the chat.
	 *
	 * @return {@code true} if the user can access the chat, {@code false} otherwise.
	 */
	public boolean userCanConnectToChat(Long userId, Long chatId) {
		// Could be replaced by a query to database
		return this.userChatRepository.findAllByUserChatPK_UserId(userId)
		                              .stream()
		                              .map(UserChat::getUserChatPK)
		                              .map(UserChatPK::getChatId)
		                              .anyMatch(aLong -> aLong.equals(chatId));
	}

	/**
	 * Retrieves the code of a chat given its name.
	 *
	 * @param chatName name of the chat.
	 *
	 * @return Returns the code of a chat given its name.
	 */
	public String getInvitationCodeByChatName(String chatName) {
		return this.chatRepository.findByName(chatName)
		                          .map(Chat::getInvitationCode)
		                          .orElseThrow(() -> new NotFoundException("Chat with name=%s does not exist".formatted(chatName)));
	}

	public List<String> getAllUsersOfChat(Long chatId) {
		return this.userChatRepository.findAllByUserChatPK_ChatId(chatId)
		                              .stream()
		                              .map(UserChat::getUserChatPK)
		                              .map(UserChatPK::getUserId)
		                              .map(this.userRepository::findById)
		                              .filter(Optional::isPresent)
		                              .map(Optional::get)
		                              .map(User::getUsername)
		                              .toList();
	}

	/**
	 * Removes a user from a chat.
	 *
	 * @param userId id of the user to remove.
	 * @param chatId id of the chat to remove the user from.
	 */
	public void removeUserFromChat(Long userId, Long chatId) {
		this.userChatRepository.deleteByUserChatPK_UserIdAndUserChatPK_ChatId(userId, chatId);
	}
}
