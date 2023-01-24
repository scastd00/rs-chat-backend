package rs.chat.domain.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserChat;
import rs.chat.domain.entity.UserChatId;
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

import static rs.chat.utils.Constants.USER;

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

		this.userChatRepository.findAllById_UserId(userId)
		                       .stream()
		                       .map(UserChat::getChat)
		                       .forEach(chatsOfUser::add);

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
			chatItem.put("name", chat.getName());
			chatItem.put("key", chat.getKey());

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
		if (this.userAlreadyBelongsToChat(userId, chatId)) {
			throw new BadRequestException("User with id=%d already belongs to chat with id=%d".formatted(userId, chatId));
		} // todo replace controller check with ControllerUtils.performActionThatMayThrowException and leave this unchanged

		User user = this.userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User with id=%d does not exist".formatted(userId)));
		Chat chat = this.chatRepository.findById(chatId).orElseThrow(() -> new BadRequestException("Chat with id=%d does not exist".formatted(chatId)));

		// Todo: check if in this type of entities second and third parameters are needed
		//  to prevent calling DB.
		this.userChatRepository.save(new UserChat(new UserChatId(userId, chatId), user, chat));
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
	 * Retrieves a chat given its key.
	 *
	 * @param key key of the chat.
	 *
	 * @return found chat.
	 */
	public Optional<Chat> getChatByKey(String key) {
		return this.chatRepository.findByKey(key);
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
		return this.userChatRepository.existsById_UserIdAndId_ChatId(userId, chatId);
	}

	/**
	 * Determines if a user can access a chat.
	 *
	 * @param userId  id of the user.
	 * @param chatKey key of the chat (can be a single number or a 2 numbers separated by an underscore).
	 *                - user-id1_id2
	 *                - otherType-id
	 *
	 * @return the key of the chat if the user can access it, null otherwise.
	 */
	public String canConnectToChat(Long userId, String chatKey) {
		if (chatKey.contains("_")) {
			String key = chatKey.split("-")[1];
			String[] userIds = key.split("_");
			Optional<Chat> chat1 = this.getChatByKey(chatKey);
			Optional<Chat> chat2 = this.getChatByKey("%s-%s_%s".formatted(USER, userIds[1], userIds[0]));

			if (chat1.isPresent() && this.userAlreadyBelongsToChat(userId, chat1.get().getId())) {
				return chat1.get().getKey();
			} else if (chat2.isPresent() && this.userAlreadyBelongsToChat(userId, chat2.get().getId())) {
				return chat2.get().getKey();
			}

			// If the user does not have a private chat with the other user, create one.
			Chat chat = this.saveChat(DomainUtils.individualChat("Chat with %s".formatted(key), key));
			this.addUserToChat(userId, chat.getId());
			this.addUserToChat(Long.parseLong(userIds[1]), chat.getId());

			return chat.getKey();
		}

		Optional<Chat> chat = this.getChatByKey(chatKey);
		return chat.isPresent() && this.userAlreadyBelongsToChat(userId, chat.get().getId())
		       ? chatKey
		       : null;
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

	/**
	 * Retrieves all the usernames that belong to a chat.
	 *
	 * @param chatId id of the chat to get the users from.
	 *
	 * @return list of usernames of the users that belong to the chat.
	 */
	public List<String> getAllUsersOfChat(Long chatId) {
		return this.userChatRepository.findAllById_ChatId(chatId)
		                              .stream()
		                              .map(UserChat::getUser)
		                              .map(User::getUsername)
		                              .toList();
	}

	/**
	 * Removes a user from a chat.
	 *
	 * @param userId  id of the user to remove.
	 * @param chatKey key of the chat to remove the user from.
	 */
	public void removeUserFromChat(Long userId, String chatKey) {
		this.getChatByKey(chatKey).ifPresent(
				chat -> this.userChatRepository.deleteById_UserIdAndId_ChatId(userId, chat.getId())
		);
	}
}
