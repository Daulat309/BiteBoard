package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;

@Component
public class UserServices 
{
	@Autowired
	private UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public List<User> getAllUser()
	{
		List<User> users = (List<User>) this.userRepository.findAll();
		return users;
	}

	public User getUser(int id)
	{
		Optional<User> optional = this.userRepository.findById(id);
		User user = optional.get();
		return user;
	}

	public User getUserByEmail(String email)
	{
	 User user = this.userRepository.findUserByUemail(email);
	 return user;
	}

	public void updateUser(User user, int id)
	{
		user.setU_id(id);
		this.userRepository.save(user);
	}

	public void deleteUser(int id)
	{
		this.userRepository.deleteById(id);
	}

	/**
	 * Registers user with BCrypt-hashed password.
	 */
	public void addUser(User user)
	{
		user.setUpassword(passwordEncoder.encode(user.getUpassword()));
		this.userRepository.save(user);
	}

	/**
	 * Validates user login — supports both plain-text (legacy) and BCrypt passwords.
	 */
	public boolean validateLoginCredentials(String email, String password)
	{
		User found = this.userRepository.findUserByUemail(email);
		if (found == null) return false;

		String stored = found.getUpassword();

		// BCrypt hashed
		if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$")))
		{
			return passwordEncoder.matches(password, stored);
		}
		// Legacy plain-text fallback
		return stored != null && stored.equals(password);
	}
}