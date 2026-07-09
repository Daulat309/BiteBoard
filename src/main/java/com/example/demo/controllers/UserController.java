package com.example.demo.controllers;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entities.User;
import com.example.demo.services.UserServices;

@Controller
public class UserController
{
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private final UserServices services;

	@Autowired
	public UserController(UserServices services) {
		this.services = services;
	}

	@PostMapping("/addingUser")
	public String addUser(@ModelAttribute User user, HttpSession session)
	{
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.services.addUser(user);
		log.info("New user added by admin: {}", user.getUemail());
		return "redirect:/admin/services";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user)
	{
		this.services.addUser(user);
		log.info("New customer self-registered: {}", user.getUemail());
		return "redirect:/login";
	}

	@GetMapping("/updatingUser/{id}")
	public String updateUser(@ModelAttribute User user, @PathVariable("id") int id, HttpSession session)
	{
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.services.updateUser(user, id);
		log.info("User updated: {}", id);
		return "redirect:/admin/services";
	}

	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable("id") int id, HttpSession session)
	{
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.services.deleteUser(id);
		log.info("User deleted: {}", id);
		return "redirect:/admin/services";
	}

}
