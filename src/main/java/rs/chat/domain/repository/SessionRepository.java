package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
	Optional<Session> findByToken(String token);

	Optional<Session> findByUserId(Long userId);

	List<Session> findAllByUserId(Long userId);
}
