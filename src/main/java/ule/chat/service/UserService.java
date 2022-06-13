package ule.chat.service;

import ule.chat.domain.User;

import java.util.List;

public interface UserService {
	List<User> getUsers();
	User saveUser(User user);
	void deleteUser(Long id);
	User getUser(String username);
	void setRoleToUser(String username, String role);
}
