package ule.chat.service;

import ule.chat.domain.Session;

import java.util.List;

public interface SessionService {
	List<Session> getSessions();

	Session saveSession(Session session);

	void deleteSession(String token);

	void deleteAllSessionsOfUser(String username);

	Session getSession(String token);

	void updateSession(String username, String accessToken, String refreshToken);
}
