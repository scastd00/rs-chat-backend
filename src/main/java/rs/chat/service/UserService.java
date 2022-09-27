package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.BadRequestException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Gets the user by username to be used by Spring Security.
	 *
	 * @param username the username identifying the user whose data is required.
	 *
	 * @return the user details.
	 *
	 * @throws UsernameNotFoundException if the user is not found.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(username);

		if (user == null) {
			log.error("User not found: {}", username);
			throw new UsernameNotFoundException(username);
		}

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole()))
		);
	}

	/**
	 * @return the list of all users in the database.
	 */
	public List<User> getUsers() {
		return this.userRepository.findAll();
	}

	public boolean existsByEmail(String email) {
		return this.userRepository.existsByEmail(email);
	}

	/**
	 * Saves a user to the database. The password must be the one received
	 * from the frontend (raw) to be encrypted correctly.
	 *
	 * @param user the user to be saved.
	 *
	 * @return the saved user.
	 */
	public User saveUser(User user) {
		if (this.existsByEmail(user.getEmail())) {
			throw new BadRequestException("Email %s taken".formatted(user.getEmail()));
		}

		log.info("Saving user: {}", user.getUsername());
		String rawPassword = user.getPassword();
		user.setPassword(this.passwordEncoder.encode(rawPassword));
		return this.userRepository.save(user);
	}

	/**
	 * Deletes a user from the database.
	 *
	 * @param id the id of the user to be deleted.
	 */
	public void deleteUser(Long id) {
		this.userRepository.deleteById(id);
	}

	/**
	 * Gets the user by username.
	 *
	 * @param username the username of the user to be retrieved.
	 *
	 * @return the user.
	 */
	public User getUser(String username) {
		return this.userRepository.findByUsername(username);
	}

	/**
	 * Establishes the role of the user.
	 *
	 * @param username the username of the user to be updated.
	 * @param role     the new role of the user.
	 */
	public void setRoleToUser(String username, String role) {
		User user = this.userRepository.findByUsername(username);
		user.setRole(role);
		this.userRepository.save(user);
	}

	public User getUserByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}

	public User getUserByCode(String code) {
		return this.userRepository.findByPasswordCode(code);
	}
}
