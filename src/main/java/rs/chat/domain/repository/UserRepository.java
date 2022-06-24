package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}