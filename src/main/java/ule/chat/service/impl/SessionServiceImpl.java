package ule.chat.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ule.chat.domain.repository.SessionRepository;
import ule.chat.service.SessionService;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionServiceImpl implements SessionService {
	private SessionRepository sessionRepository;

	@Override
	public void removeSession(String token) {
		log.info("Removing session: {}", token);
		this.sessionRepository.findByToken(token)
		                      .ifPresent(this.sessionRepository::delete);
	}
}
