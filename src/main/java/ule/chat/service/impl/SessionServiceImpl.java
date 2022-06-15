package ule.chat.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ule.chat.domain.Session;
import ule.chat.domain.User;
import ule.chat.domain.repository.SessionRepository;
import ule.chat.domain.repository.UserRepository;
import ule.chat.service.SessionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionServiceImpl implements SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;

	@Override
	public List<Session> getSessions() {
		return this.sessionRepository.findAll();
	}

	@Override
	public Session saveSession(Session session) {
		return this.sessionRepository.save(session);
	}

	@Override
	public void deleteSession(String token) {
		log.info("Removing session: {}", token);
		this.sessionRepository.findByAccessToken(token)
		                      .ifPresent(this.sessionRepository::delete);
	}

	@Override
	public void deleteAllSessionsOfUser(String username) {
		User user = this.userRepository.findByUsername(username);
		this.sessionRepository.deleteAll(this.sessionRepository.findAllByUserId(user.getId()));
	}

	@Override
	public Session getSession(String token) {
		return this.sessionRepository.findByAccessToken(token)
		                             .orElse(null);
	}

	@Override
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
