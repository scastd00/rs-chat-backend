package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Friend;
import rs.chat.domain.entity.FriendId;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.FriendRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.NotFoundException;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FriendService {
	private final FriendRepository friendRepository;
	private final UserRepository userRepository;
	private final Clock clock;

	/**
	 * Gets a user by its username.
	 *
	 * @param username The username of the user.
	 *
	 * @return The user.
	 *
	 * @implNote This method is private because it is only used internally.
	 * The tested implementation is in {@link rs.chat.domain.service.UserService#getUserByUsername(String)}.
	 */
	private User getUserByUsername(String username) {
		return this.userRepository.findByUsername(username)
		                          .orElseThrow(() -> new NotFoundException("User not found"));
	}

	/**
	 * Switches the state of a friend. If the friend is already added, it will be removed.
	 * If the friend is not added, it will be added.
	 *
	 * @param username       The username of the user.
	 * @param friendUsername The username of the friend to add or remove.
	 *
	 * @return {@code true} if the friend was added, {@code false} if the friend was removed.
	 */
	public boolean switchFriendState(String username, String friendUsername) {
		User user = this.getUserByUsername(username);
		User friend = this.getUserByUsername(friendUsername);

		FriendId id = new FriendId(user.getId(), friend.getId());
		Friend entity = new Friend(id, user, friend, this.clock.instant());

		// If the friend is already added, remove it, otherwise add it.
		if (this.areFriends(id)) {
			this.friendRepository.delete(entity);
			return false;
		}

		this.friendRepository.save(entity);
		return true;
	}

	/**
	 * Checks if two users are friends.
	 *
	 * @param id The id of the relationship to check.
	 *
	 * @return {@code true} if the users are friends, {@code false} otherwise.
	 */
	private boolean areFriends(FriendId id) {
		return this.friendRepository.existsById(id);
	}
}
