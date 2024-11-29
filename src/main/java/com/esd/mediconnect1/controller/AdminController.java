package com.esd.mediconnect1.controller;

import com.esd.mediconnect1.dao.AppointmentDAO;
import com.esd.mediconnect1.dao.DoctorPatientDAO;
import com.esd.mediconnect1.dao.NotificationDAO;
import com.esd.mediconnect1.dao.PatientNoteDAO;
import com.esd.mediconnect1.dao.PatientProfileDAO;
import com.esd.mediconnect1.dao.PrescriptionDAO;
import com.esd.mediconnect1.dao.UserDAO;
import com.esd.mediconnect1.model.Prescription;
import com.esd.mediconnect1.model.User;

import org.springframework.ui.Model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private  UserDAO userDao;
	@Autowired
	private  PasswordEncoder passwordEncoder;
	@Autowired
	private DoctorPatientDAO doctorPatientDAO;
	@Autowired
	private PrescriptionDAO prescriptionDAO;
	@Autowired
	private AppointmentDAO appointmentDAO;
	@Autowired
	private NotificationDAO notificationDAO;
	@Autowired
	private PatientProfileDAO patientProfileDAO;
	@Autowired
	private PatientNoteDAO patientNoteDAO;
	
	@PostMapping("/create-admin")
	public ResponseEntity<String> createAdmin(@RequestBody User user) {
		// Check if an admin user already exists
		if (userDao.adminExists()) {
			return ResponseEntity.badRequest().body("Admin user already exists.");
		}

		// Set admin role
		user.setRole("ADMIN");
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Save admin user
		userDao.saveUser(user);
		return ResponseEntity.ok("Admin user created successfully.");
	}

	// Admin dashboard showing user management options
	@GetMapping("/dashboard")
	public String adminDashboard(Model model) {
		List<User> users = userDao.getAllUsers();
		model.addAttribute("users", users);
		return "admin-dashboard";
	}

	@GetMapping("/create-user")
	public String showCreateUserForm(Model model) {
		model.addAttribute("user", new User());
		return "create-user";
	}

	// Handle the submission of the create user form
	@PostMapping("/create-user")
	public String createUser(@ModelAttribute User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userDao.saveUser(user);
		return "redirect:/api/admin/dashboard";
	}

	@GetMapping("/edit-user/{id}")
	public String showEditUserForm(@PathVariable("id") Long id, Model model) {
		User user = userDao.getUserById(id);
		model.addAttribute("user", user);
		return "edit-user";
	}

	// Handle the submission of the edit user form
	@PostMapping("/edit-user")
	public String editUser(@ModelAttribute User user) {
		userDao.updateUser(user);
		return "redirect:/api/admin/dashboard";
	}

	// Delete a user
	@GetMapping("/delete-user/{id}")
	public String deleteUser(@PathVariable("id") Long id) {
		User user = userDao.getUserById(id);
		if (user == null) {
	        throw new RuntimeException("User not found");
	    }
		
		if ("DOCTOR".equals(user.getRole())) {
	        doctorPatientDAO.deleteByDoctorId(user.getId());
	        prescriptionDAO.deleteByDoctorId(user.getId());
	        appointmentDAO.deleteByDoctorId(user.getId());
	    } else if ("PATIENT".equals(user.getRole())) {
	        patientNoteDAO.deleteByPatientId(user.getId());
	    	patientProfileDAO.deleteByUserId(user.getId());
	    	doctorPatientDAO.deleteByPatientId(user.getId());
	        prescriptionDAO.deleteByPatientId(user.getId());
	        appointmentDAO.deleteByPatientId(user.getId());
	        
	    }
	    notificationDAO.deleteByUserId(user.getId());

		userDao.deleteUser(id);
		
		
		return "redirect:/api/admin/dashboard";
	}
}