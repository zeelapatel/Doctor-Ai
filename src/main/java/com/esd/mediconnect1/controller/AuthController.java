package com.esd.mediconnect1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.esd.mediconnect1.dao.NotificationDAO;
import com.esd.mediconnect1.dao.UserDAO;
import com.esd.mediconnect1.model.Notification;
import com.esd.mediconnect1.model.User;

@Controller
public class AuthController {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private NotificationDAO notificationDAO;
	@Autowired
	private Email emailService;

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/")
	public String defaultPage() {
		return "login";
	}

	@GetMapping("/register")
	public String registerPatient() {
		return "register-patient";
	}

	@PostMapping("/register-patient")
	public String registerPatient(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("email") String email, Model model) {

		// Check if the username is already taken
		if (userDAO.findByUsername(username) != null) {
			model.addAttribute("errorMessage", "Username is already taken.");
			return "register-patient";
		}

		// Create a new patient user
		User patient = new User();
		patient.setUsername(username);
		patient.setPassword(passwordEncoder.encode(password));
		patient.setRole("PATIENT");

		userDAO.saveUser(patient);
		
		emailService.sendEmail(email, "Your Account Credentials",
				"Please Complet your Profile \n\nUsername: " + username + "\nPassword: " + password);
		Notification notification = new Notification();
		notification.setUser(patient);
		notification.setMessage("Please complete your profile with your name, age, and medical history.");
		notificationDAO.saveNotification(notification);

		model.addAttribute("successMessage", "Registration successful. Please login.");
		return "redirect:/login";
	}

}