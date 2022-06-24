package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.Session;
import rs.chat.domain.User;
import rs.chat.domain.repository.SessionRepository;
import rs.chat.domain.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;

	public List<Session> getSessions() {
		return this.sessionRepository.findAll();
	}

	public Session saveSession(Session session) {
		return this.sessionRepository.save(session);
	}

	public void deleteSession(String token) {
		log.info("Removing session: {}", token);
		this.sessionRepository.findByAccessToken(token)
		                      .ifPresent(this.sessionRepository::delete);
	}

	public void deleteAllSessionsOfUser(String username) {
		User user = this.userRepository.findByUsername(username);
		this.sessionRepository.deleteAll(this.sessionRepository.findAllByUserId(user.getId()));
	}

	public Session getSession(String token) {
		return this.sessionRepository.findByAccessToken(token)
		                             .orElse(null);
	}

	public void updateSession(String username, String accessToken, String refreshToken) {
		User user = this.userRepository.findByUsername(username);
		this.sessionRepository.findByUserId(user.getId())
		                      .ifPresent(session -> {
			                      session.setAccessToken(accessToken);
			                      session.setRefreshToken(refreshToken);
			                      this.sessionRepository.save(session);
		                      });
	}
}
