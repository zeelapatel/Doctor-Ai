package com.esd.mediconnect1.controller;

import com.esd.mediconnect1.dao.AppointmentDAO;
import com.esd.mediconnect1.dao.DoctorPatientDAO;
import com.esd.mediconnect1.dao.DoctorProfileDAO;
import com.esd.mediconnect1.dao.MedicineInventoryDAO;
import com.esd.mediconnect1.dao.NotificationDAO;
import com.esd.mediconnect1.dao.PatientNoteDAO;
import com.esd.mediconnect1.dao.PatientProfileDAO;
import com.esd.mediconnect1.dao.PrescriptionDAO;
import com.esd.mediconnect1.dao.UserDAO;
import com.esd.mediconnect1.model.Appointment;
import com.esd.mediconnect1.model.AppointmentStatus;
import com.esd.mediconnect1.model.DoctorPatient;
import com.esd.mediconnect1.model.DoctorProfile;
import com.esd.mediconnect1.model.MedicineInventory;
import com.esd.mediconnect1.model.Notification;
import com.esd.mediconnect1.model.PatientNote;
import com.esd.mediconnect1.model.PatientProfile;
import com.esd.mediconnect1.model.Prescription;
import com.esd.mediconnect1.model.PrescriptionStatus;
import com.esd.mediconnect1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;
import javax.print.Doc;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

	@Autowired
	private AppointmentDAO appointmentDAO;
	@Autowired
	private PrescriptionDAO prescriptionDAO;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private DoctorPatientDAO doctorPatientDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private NotificationDAO notificationDAO;
	@Autowired
	private PatientProfileDAO patientProfileDAO;
	@Autowired
	private PatientNoteDAO patientNoteDAO;
	@Autowired
	private MedicineInventoryDAO medicineInventoryDAO;
	@Autowired
	private Email emailService;
	@Autowired
	private DoctorProfileDAO doctorProfileDAO;
	
	

	@Autowired
	public DoctorController(AppointmentDAO appointmentService, PrescriptionDAO prescriptionService,
			UserDAO userService) {
		this.appointmentDAO = appointmentService;
		this.prescriptionDAO = prescriptionService;
		this.userDao = userService;
	}

	@GetMapping("/dashboard")
	public String doctorDashboard(Model model, Authentication authentication) {
		User doctor = userDao.findByUsername(authentication.getName());
		if (doctor == null)
			throw new RuntimeException("Doctor not found");
		model.addAttribute("doctor", doctor);
		model.addAttribute("message", "Welcome to your dashboard!");

		List<Notification> notifications = notificationDAO.getNotificationsByUserId(doctor.getId());
		model.addAttribute("notifications", notifications);

		return "doctor-dashboard";
	}

//	

	@GetMapping("/prescriptions")
	public String createPrescription(Model model, Authentication authentication) {
		User doctor = userDao.findByUsername(authentication.getName());
		if (doctor == null) {
			throw new RuntimeException("Doctor not found");
		}

		List<DoctorPatient> assignments = doctorPatientDAO.getAssignmentsByDoctorId(doctor.getId());
		List<User> patients = assignments.stream().map(DoctorPatient::getPatient).collect(Collectors.toList());
		List<MedicineInventory> medicines = medicineInventoryDAO.findAll();

		model.addAttribute("patients", patients);
		model.addAttribute("medicines", medicines);
		return "create-prescription";
	}

	@PostMapping("/prescriptions/create")
	public String createPrescription(@RequestParam("patientId") Long patientId,
			@RequestParam("medication") String medication, @RequestParam("dosage") String dosage,
			Authentication authentication, Model model) {

		User doctor = userDao.findByUsername(authentication.getName());
		User patient = userDao.findPatientById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));
		if (doctor != null && patient != null) {
			Prescription prescription = new Prescription();
			prescription.setDoctor(doctor);
			prescription.setPatient(patient);
			prescription.setMedication(medication);
			prescription.setDosage(dosage);
			prescription.setIssueDate(LocalDateTime.now());
			prescription.setStatus(PrescriptionStatus.valueOf("PENDING"));

			prescriptionDAO.savePrescription(prescription);
			model.addAttribute("successMessage", "Prescription created successfully!");
		}

		return "redirect:/doctor/dashboard";
	}

	@GetMapping("/patient/create")
	public String showAddPatientForm() {
		return "add-patient";
	}

	@PostMapping("/patient/create")
	public String createPatient(@RequestParam("username") String patientUsername,
			@RequestParam("password") String password, @RequestParam("email") String email,
			Authentication authentication) {
		// Fetch the logged-in doctor
		String username = authentication.getName();
		User doctor = userDao.findByUsername(username);
		User existingUser = userDao.findByUsername(patientUsername);
		if (existingUser != null) {
			throw new IllegalArgumentException("Username '" + patientUsername + "' is already taken.");
		}
		if (doctor == null) {
			throw new RuntimeException("Doctor not found");
		}

		User patient = new User();
		patient.setPassword(passwordEncoder.encode(password));
		patient.setUsername(patientUsername);
		patient.setRole("PATIENT");

		userDao.saveUser(patient);

		DoctorPatient doctorPatient = new DoctorPatient();
		doctorPatient.setDoctor(doctor);
		doctorPatient.setPatient(patient);
		doctorPatientDAO.saveAssignment(doctorPatient);

		PatientProfile patientProfile = new PatientProfile();
		patientProfile.setAge(null);
		patientProfile.setAddress(null);
		patientProfile.setEmail(email);
		patientProfile.setFirstName(null);
		patientProfile.setLastName(null);
		patientProfile.setMobile(null);
		patientProfile.setCreatedAt(LocalDateTime.now());
		patientProfile.setMedicalHistory(null);
		patientProfile.setUpdatedAt(LocalDateTime.now());
		patientProfile.setUser(patient);

		patientProfileDAO.savePatientProfile(patientProfile);
		emailService.sendEmail(email, "Your Account Credentials",
				"Please Complet your Profile \n\nUsername: " + patientUsername + "\nPassword: " + password);
		Notification notification = new Notification();
		notification.setUser(patient);
		notification.setMessage("Please complete your profile with your name, age, and medical history.");
		notificationDAO.saveNotification(notification);

		return "redirect:/doctor/dashboard";
	}

	@GetMapping("/patients")
	public String managePatients(Model model, Authentication authentication) {
		User doctor = userDao.findByUsername(authentication.getName());
		if (doctor == null)
			throw new RuntimeException("Doctor not found");
		List<DoctorPatient> assignments = doctorPatientDAO.getAssignmentsByDoctorId(doctor.getId());
		List<User> patients = assignments.stream().map(DoctorPatient::getPatient).collect(Collectors.toList());
		model.addAttribute("patients", patients);
		return "manage-patients";
	}

	@GetMapping("/patient/add-note/{id}")
	public String showAddNoteForm(@PathVariable Long id, Model model) {
		model.addAttribute("patientId", id); // Pass the patient ID to the form
		return "add-patient-note"; // Redirect to the form template
	}

	@PostMapping("/patient/add-note/{id}")
	public String addNote(@PathVariable Long id, @RequestParam("note") String note, Authentication authentication) {
		User doctor = userDao.findByUsername(authentication.getName());
		User patient = userDao.getUserById(id);

		if (doctor == null || patient == null) {
			throw new RuntimeException("Doctor or Patient not found");
		}

		PatientNote patientNote = new PatientNote();
		patientNote.setDoctor(doctor);
		patientNote.setPatient(patient);
		patientNote.setNote(note);

		patientNoteDAO.saveNote(patientNote);

		Notification notification = new Notification();
		notification.setUser(patient);
		notification.setMessage("A new note has been added to your profile by Dr. " + doctor.getUsername());
		notificationDAO.saveNotification(notification);

		return "redirect:/doctor/patients";
	}

	@GetMapping("/appointments")
	public String manageAppointments(Model model, Authentication authentication) {
		User doctor = userDao.findByUsername(authentication.getName());
		if (doctor == null)
			throw new RuntimeException("Doctor not found");
		List<Appointment> appointments = appointmentDAO.getAppointmentsByDoctorId(doctor.getId());
		model.addAttribute("appointments", appointments);
		return "manage-appointments";
	}

	@PostMapping("/notification/read/{id}")
	public String markNotificationAsRead(@PathVariable Long id) {
		notificationDAO.markAsRead(id);
		return "redirect:/doctor/dashboard";
	}

	@PostMapping("/appointment/update-status/{id}")
	public String updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
		Appointment appointment = appointmentDAO.getAppointmentById(id);
		if (appointment != null) {
			appointment.setStatus(AppointmentStatus.valueOf(status));
			appointmentDAO.updateAppointment(appointment);

			// Notify patient about status change
			Notification notification = new Notification();
			notification.setUser(appointment.getPatient());
			notification.setMessage("Your appointment on " + appointment.getAppointmentDate() + " at "
					+ appointment.getAppointmentTime() + " has been " + status.toLowerCase());
			notificationDAO.saveNotification(notification);
		}
		return "redirect:/doctor/appointments";
	}

	@GetMapping("/patient/view/{id}")
	public String viewPatientProfile(@PathVariable Long id, Model model, Authentication authentication) {
		User patient = userDao.getUserById(id);

		if (patient == null) {
			throw new RuntimeException("Patient not found");
		}

		Optional<PatientProfile> profileOptional = patientProfileDAO.getPatientProfileByUserId(patient.getId());
		PatientProfile profile = profileOptional.orElse(new PatientProfile());
		profile.setUser(patient);

		model.addAttribute("profile", profile);
		model.addAttribute("user", "doctor");
		return "view-profile";
	}
	
	@GetMapping("/view-profile")
	public String viewProfile(Model model, Authentication authentication) {
		String username = authentication.getName();
		User doctor = userDao.findByUsername(username);
		
		if ( doctor == null) {
			throw new RuntimeException("Patient not found");
		}
		
	Optional<DoctorProfile> profileOptional = doctorProfileDAO.getDoctorProfileByUserId(doctor.getId());	
	DoctorProfile profile = profileOptional.orElse(new DoctorProfile());

	profile.setUser(doctor);

	model.addAttribute("profile", profile);
	model.addAttribute("user", "doctor");
	return "doctor-profile";

	}
	
	@GetMapping("/profile/update")
	public String updateProfileForm(Model model, Authentication authentication) {
		String username = authentication.getName();
		User doctor = userDao.findByUsername(username);
		if (doctor == null) {
			throw new RuntimeException("doctor not found");
		}
		

		Optional<DoctorProfile> profileOptional = doctorProfileDAO.getDoctorProfileByUserId(doctor.getId());
		DoctorProfile profile = profileOptional.orElse(new DoctorProfile());
		profile.setUser(doctor);
		model.addAttribute("profile", profile);
		return "update-doctor-profile";
	}
	
	@PostMapping("/profile/update")
	public String updateProfile(@ModelAttribute DoctorProfile updatedProfile, Authentication authentication) {
	    String username = authentication.getName();
	    User doctor = userDao.findByUsername(username);

	    if (doctor == null) {
	        throw new RuntimeException("Doctor not found");
	    }

	    Optional<DoctorProfile> existingProfile = doctorProfileDAO.getDoctorProfileByUserId(doctor.getId());
	    if (existingProfile.isPresent()) {
	    	DoctorProfile profile = existingProfile.get();
	        profile.setFirstName(updatedProfile.getFirstName());
	        profile.setLastName(updatedProfile.getLastName());
	      
	        profile.setMobile(updatedProfile.getMobile());
	        profile.setEmail(updatedProfile.getEmail());
	 
	        profile.setUpdatedAt(LocalDateTime.now());
	        doctorProfileDAO.updateDoctorProfile(profile);
	    } else {
	        updatedProfile.setUser(doctor);
	        updatedProfile.setCreatedAt(LocalDateTime.now());
	        updatedProfile.setUpdatedAt(LocalDateTime.now());
	        doctorProfileDAO.saveDoctorProfile(updatedProfile);
	    }
	    return "redirect:/doctor/dashboard";
	}
	

}
