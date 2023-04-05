package rs.chat.domain.service;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.dtos.UserDto;
import rs.chat.domain.entity.mappers.UserMapper;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;

	/**
	 * Gets the user by username to be used by Spring Security.
	 *
	 * @param username the username identifying the user whose data is required.
	 *
	 * @return the user details.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = this.getUserByUsername(username);

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				List.of(new SimpleGrantedAuthority(user.getRole()))
		);
	}

	/**
	 * @return the list of all users in the database.
	 */
	public List<UserDto> getUsers() {
		return this.userRepository.findAll()
		                          .stream()
		                          .map(userMapper::toDto)
		                          .toList();
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
	public User createUser(User user) {
		if (this.existsByEmail(user.getEmail())) {
			throw new BadRequestException("Email %s taken".formatted(user.getEmail()));
		}

		String rawPassword = user.getPassword();
		user.setPassword(this.passwordEncoder.encode(rawPassword));
		return this.userRepository.save(user);
	}

	/**
	 * Saves a user to the database.
	 *
	 * @param user the user to be updated.
	 */
	public User saveUser(User user) {
		return this.userRepository.save(user);
	}

	/**
	 * Changes the password of a user (by encrypting it).
	 *
	 * @param user the user whose password is to be changed.
	 */
	public void changePassword(User user) {
		String rawPassword = user.getPassword();
		user.setPassword(this.passwordEncoder.encode(rawPassword));
		this.userRepository.save(user);
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
	public User getUserByUsername(String username) {
		return this.userRepository.findByUsername(username)
		                          .orElseThrow(() -> new NotFoundException("Username %s not found".formatted(username)));
	}

	/**
	 * Establishes the role of the user.
	 *
	 * @param username the username of the user to be updated.
	 * @param role     the new role of the user.
	 */
	public void setRoleToUser(String username, String role) {
		User user = this.userRepository.findByUsername(username)
		                               .orElseThrow(() -> new NotFoundException("Username %s not found".formatted(username)));
		user.setRole(role);
		this.userRepository.save(user);
	}

	public User getUserByEmail(String email) {
		return this.userRepository.findByEmail(email)
		                          .orElseThrow(() -> new NotFoundException("Email %s not found".formatted(email)));
	}

	public User getUserByCode(String code) {
		return this.userRepository.findByPasswordCode(code)
		                          .orElseThrow(() -> new NotFoundException("Code %s not found".formatted(code)));
	}

	public User getUserById(Long userId) {
		return this.userRepository.findById(userId)
		                          .orElseThrow(() -> new NotFoundException("User with id %d not found".formatted(userId)));
	}

	public JsonObject getUserStats(String username) {
		return this.getUserByUsername(username).getMessageCountByType();
	}
}
