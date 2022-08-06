package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.Chat;
import rs.chat.domain.repository.ChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
	private final ChatRepository chatRepository;

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
}
