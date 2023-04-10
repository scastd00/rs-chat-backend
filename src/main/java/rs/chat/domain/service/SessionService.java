package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.SessionRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.NotFoundException;

import java.time.Clock;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@EnableScheduling
public class SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;
	private final Clock clock;

	public List<Session> getAll() {
		return this.sessionRepository.findAll();
	}

	/**
	 * Saves a new session in the database.
	 *
	 * @param session the session to be saved in the database.
	 *
	 * @return the saved session.
	 */
	public Session saveSession(Session session) {
		return this.sessionRepository.save(session);
	}

	/**
	 * Deletes a session given its token.
	 *
	 * @param token the token of the session to be deleted.
	 */
	public void deleteSession(String token) {
		this.sessionRepository.findByToken(token)
		                      .ifPresent(this.sessionRepository::delete);
	}

	public void deleteAllById(List<Long> ids) {
		this.sessionRepository.deleteAllById(ids);
	}

	/**
	 * Deletes all sessions of a user given its username.
	 *
	 * @param username the username of the user whose sessions are to be deleted.
	 */
	public void deleteAllSessionsOfUser(String username) {
		User user = this.userRepository.findByUsername(username)
		                               .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
		this.sessionRepository.deleteAll(this.sessionRepository.findAllByUserId(user.getId()));
	}

	/**
	 * Finds a session given its token.
	 *
	 * @param token the token of the session to be found.
	 *
	 * @return the found session.
	 */
	public Session getSessionByToken(String token) {
		return this.sessionRepository.findByToken(token)
		                             .orElseThrow(() -> new NotFoundException("Session not found."));
	}

	/**
	 * Checks whether a session is expired.
	 *
	 * @param session the session to be checked.
	 *
	 * @return {@code true} if the session is expired, {@code false} otherwise.
	 */
	public boolean isExpiredSession(Session session) {
		return session.getEndDate().isBefore(this.clock.instant());
	}

	/**
	 * Finds all sessions of a user given its username.
	 *
	 * @param username the username of the user whose sessions are to be found.
	 *
	 * @return the found sessions.
	 */
	public List<Session> getSessionsByUsername(String username) {
		return this.userRepository.findByUsername(username)
		                          .stream()
		                          .map(User::getSessions)
		                          .flatMap(Set::stream)
		                          .toList();
	}

	/**
	 * Updates a session of the given user with the given token.
	 *
	 * @param username the username of the user whose session is to be updated.
	 * @param token    the token of the session to be updated.
	 */
	public void updateSession(String username, String token) {
		User user = this.userRepository.findByUsername(username)
		                               .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
		this.sessionRepository.findByUserId(user.getId())
		                      .ifPresent(session -> {
			                      session.setToken(token);
			                      this.sessionRepository.save(session);
		                      });
	}
}
