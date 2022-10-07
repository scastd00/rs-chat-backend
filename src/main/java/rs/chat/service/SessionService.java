package rs.chat.service;

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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@EnableScheduling
public class SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;

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
	 * Deletes a session given its access token.
	 *
	 * @param token the access token of the session to be deleted.
	 */
	public void deleteSession(String token) {
		log.info("Removing session: {}", token);
		this.sessionRepository.findByAccessToken(token)
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
	 * Finds a session given its access token.
	 *
	 * @param token the access token of the session to be found.
	 *
	 * @return the found session.
	 */
	public Session getSession(String token) {
		return this.sessionRepository.findByAccessToken(token)
		                             .orElse(null);
	}

	/**
	 * Finds all sessions of a user given its username.
	 *
	 * @param username the username of the user whose sessions are to be found.
	 *
	 * @return the found sessions.
	 */
	public List<String> getSessionsOfUser(String username) {
		Long userId = this.userRepository.findByUsername(username)
		                                 .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)))
		                                 .getId();
		return this.sessionRepository.findAllByUserId(userId)
		                             .stream()
		                             .map(Session::getSrcIp)
		                             .toList();
	}

	/**
	 * Updates a session of the given user with the given access and refresh tokens.
	 *
	 * @param username     the username of the user whose session is to be updated.
	 * @param accessToken  the access token of the session to be updated.
	 * @param refreshToken the refresh token of the session to be updated.
	 */
	public void updateSession(String username, String accessToken, String refreshToken) {
		User user = this.userRepository.findByUsername(username)
		                               .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
		this.sessionRepository.findByUserId(user.getId())
		                      .ifPresent(session -> {
			                      session.setAccessToken(accessToken);
			                      session.setRefreshToken(refreshToken);
			                      this.sessionRepository.save(session);
		                      });
	}
}
