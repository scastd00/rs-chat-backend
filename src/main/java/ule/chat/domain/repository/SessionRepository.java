package ule.chat.domain.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ule.chat.domain.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
	@NotNull Optional<Session> findByAccessToken(String token);
	@NotNull Optional<Session> findByUserId(Long userId);
	List<Session> findAllByUserId(Long userId);
}
