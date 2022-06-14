package ule.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ule.chat.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
