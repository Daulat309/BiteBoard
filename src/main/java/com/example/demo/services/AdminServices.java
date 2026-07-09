package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;

@Component
public class AdminServices
{
	@Autowired
	private AdminRepository adminRepository;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public List<Admin>getAll()
	{
		 List<Admin> admins = (List<Admin>)this.adminRepository.findAll();
		 return admins;
	}

	public Admin getAdmin(int id)
	{
		Optional<Admin> optional = this.adminRepository.findById(id);
		Admin admin=optional.get();
		return admin;
	}

	public void update(Admin admin ,int id)
	{
		for (Admin ad : getAll()) 
		{
			if(ad.getAdminId()==id)
			{
				this.adminRepository.save(admin);
			}
		}
	}
	
	public void delete(int id)
	{
		this.adminRepository.deleteById(id);
	}

	/**
	 * Saves admin with BCrypt-hashed password.
	 */
	public void addAdmin(Admin admin)
	{
		// Hash the password before saving
		admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
		this.adminRepository.save(admin);
	}

	/**
	 * Validates admin login — supports both plain-text (legacy) and BCrypt hashed passwords.
	 * This ensures backward compatibility with the seeded admin record.
	 */
	public boolean validateAdminCredentials(String email, String password)
	{
		Admin admin = adminRepository.findByAdminEmail(email);
		if (admin == null) return false;

		String stored = admin.getAdminPassword();

		// If stored password is BCrypt (starts with $2a$), use matches()
		if (stored.startsWith("$2a$") || stored.startsWith("$2b$"))
		{
			return passwordEncoder.matches(password, stored);
		}
		// Legacy plain-text fallback (for seeded admin records)
		return stored.equals(password);
	}
}