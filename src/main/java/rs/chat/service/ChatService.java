package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.UserChat;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.UserChatRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
	private final ChatRepository chatRepository;
	private final UserChatRepository userChatRepository;

	public List<Chat> getAllChats() {
		return this.chatRepository.findAll();
	}

	public String getChatMetadata(String chatName) {
		Chat chat = this.chatRepository.findByName(chatName);
		return chat != null ? chat.getMetadata() : null;
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

	public List<Chat> getAllChatsOfUser(Long userId) {
		List<Chat> chatsOfUser = new ArrayList<>();

		this.userChatRepository.findAllByUserId(userId)
		                       .stream()
		                       .map(UserChat::getChatId)
		                       .map(this.chatRepository::findById)
		                       .forEach(chat -> chat.ifPresent(chatsOfUser::add));

		return chatsOfUser;
	}
}
