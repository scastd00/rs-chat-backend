package ule.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ule.chat.domain.User;
import ule.chat.domain.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(username);

		if (user == null) {
			log.error("User not found: {}", username);
			throw new UsernameNotFoundException(username);
		}

		log.info("User found: {}, Role: {}", username, user.getRole());

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), user.getPassword(), List.of(user.getSimpleGrantedAuthority())
		);
	}

	public List<User> getUsers() {
		return this.userRepository.findAll();
	}

	/**
	 * Saves a user to the database. The password must be the
	 * one received from the frontend (raw).
	 *
	 * @param user
	 * @return
	 */
	public User saveUser(User user) {
		log.info("Saving user: {}", user.getUsername());
		String rawPassword = user.getPassword();
		user.setPassword(this.passwordEncoder.encode(rawPassword));
		return this.userRepository.save(user);
	}

	public void deleteUser(Long id) {
		this.userRepository.deleteById(id);
	}

	public User getUser(String username) {
		return this.userRepository.findByUsername(username);
	}

	public void setRoleToUser(String username, String role) {
		User user = this.userRepository.findByUsername(username);
		user.setRole(role);
		this.userRepository.save(user);
	}
}
