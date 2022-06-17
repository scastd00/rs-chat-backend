package ule.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ule.chat.domain.Session;
import ule.chat.domain.User;
import ule.chat.domain.repository.SessionRepository;
import ule.chat.domain.repository.UserRepository;
import ule.chat.exceptions.NotFoundException;

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
		Session session = this.sessionRepository.findByAccessToken(token)
		                                        .orElseThrow(() -> new NotFoundException("Session not found"));
		this.sessionRepository.delete(session);
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
