package ule.chat.service.impl;

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
import ule.chat.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(username);

		if (user == null) {
			log.error("User not found: {}", username);
			throw new UsernameNotFoundException(username);
		}

		log.info("User found: {}", username);

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), user.getPassword(), List.of(user.getSimpleGrantedAuthority())
		);
	}

	@Override
	public List<User> getUsers() {
		return this.userRepository.findAll();
	}

	@Override
	public User saveUser(User user) {
		log.info("Saving user: {}", user.getUsername());
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		return this.userRepository.save(user);
	}

	@Override
	public void deleteUser(Long id) {
		this.userRepository.deleteById(id);
	}

	@Override
	public User getUser(String username) {
		return this.userRepository.findByUsername(username);
	}

	@Override
	public void setRoleToUser(String username, String role) {
		User user = this.userRepository.findByUsername(username);
		user.setRole(role);
		this.userRepository.save(user);
	}
}
