package ule.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ule.chat.domain.User;
import ule.chat.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public List<User> getUsers() {
		return userRepository.findAll(Sort.by("username"));
	}

	@Override
	public User saveUser(User user) {
		log.info("Saving user: {}", user.getUsername());
		return userRepository.save(user);
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public User getUser(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public void setRoleToUser(String username, String role) {
		User user = userRepository.findByUsername(username);
		user.setRole(role);
		userRepository.save(user);
	}
}
