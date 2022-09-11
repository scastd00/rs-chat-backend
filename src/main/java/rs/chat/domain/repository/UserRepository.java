package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	User findByEmail(String email);

	User findByPasswordCode(String passwordCode);
}
