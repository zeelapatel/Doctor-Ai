package com.esd.mediconnect1.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.esd.mediconnect1.dao.AppointmentDAO;
import com.esd.mediconnect1.dao.DoctorProfileDAO;
import com.esd.mediconnect1.dao.NotificationDAO;
import com.esd.mediconnect1.dao.PatientNoteDAO;
import com.esd.mediconnect1.dao.PatientProfileDAO;
import com.esd.mediconnect1.dao.PrescriptionDAO;
import com.esd.mediconnect1.dao.UserDAO;
import com.esd.mediconnect1.model.Appointment;
import com.esd.mediconnect1.model.Notification;
import com.esd.mediconnect1.model.PatientNote;
import com.esd.mediconnect1.model.PatientProfile;
import com.esd.mediconnect1.model.Prescription;
import com.esd.mediconnect1.model.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/patient")
public class PatientController {

	@Autowired
	private PatientProfileDAO patientProfileDAO;
	@Autowired
	private AppointmentDAO appointmentDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private NotificationDAO notificationDAO;
	@Autowired
	private PatientNoteDAO patientNoteDAO;
	@Autowired
	private PrescriptionDAO prescriptionDAO;
	@Autowired
	private DoctorProfileDAO doctorProfileDAO;
	
	@GetMapping("/dashboard")
	public String patientDashboard(Model model, Authentication authentication, HttpServletRequest request) {
		

		String username = authentication.getName();
		User patient = userDAO.findByUsername(username);

		if (patient == null) {
			throw new RuntimeException("Patient not found");
		}

		List<Notification> notifications = notificationDAO.getNotificationsByUserId(patient.getId());
		model.addAttribute("notifications", notifications);
		model.addAttribute("message", "Welcome to your dashboard!");
		return "patient-dashboard";
	}

	@PostMapping("/notification/read/{id}")
	public String markNotificationAsRead(@PathVariable Long id) {
		notificationDAO.markAsRead(id);
		return "redirect:/patient/dashboard";
	}

	@GetMapping("/profile/view")
	public String viewProfile(Model model, Authentication authentication) {
		String username = authentication.getName();
		User patient = userDAO.findByUsername(username);

		if (patient == null) {
			throw new RuntimeException("Patient not found");
		}

		Optional<PatientProfile> profileOptional = patientProfileDAO.getPatientProfileByUserId(patient.getId());
		PatientProfile profile = profileOptional.orElse(new PatientProfile());
		profile.setUser(patient);

		model.addAttribute("profile", profile);
		model.addAttribute("user", "patient");

		return "view-profile";
	}

	@GetMapping("/profile/update")
	public String updateProfileForm(Model model, Authentication authentication) {
		String username = authentication.getName();
		User patient = userDAO.findByUsername(username);

		if (patient == null) {
			throw new RuntimeException("Patient not found");
		}

		Optional<PatientProfile> profileOptional = patientProfileDAO.getPatientProfileByUserId(patient.getId());
		PatientProfile profile = profileOptional.orElse(new PatientProfile());
		profile.setUser(patient);

		model.addAttribute("profile", profile);
		return "update-patient-profile";
	}

	@PostMapping("/profile/update")
	public String updateProfile(@ModelAttribute PatientProfile updatedProfile, Authentication authentication) {
	    String username = authentication.getName();
	    User patient = userDAO.findByUsername(username);

	    if (patient == null) {
	        throw new RuntimeException("Patient not found");
	    }

	    Optional<PatientProfile> existingProfile = patientProfileDAO.getPatientProfileByUserId(patient.getId());
	    if (existingProfile.isPresent()) {
	        PatientProfile profile = existingProfile.get();
	        profile.setFirstName(updatedProfile.getFirstName());
	        profile.setLastName(updatedProfile.getLastName());
	        profile.setAge(updatedProfile.getAge());
	        profile.setAddress(updatedProfile.getAddress());
	        profile.setMobile(updatedProfile.getMobile());
	        profile.setEmail(updatedProfile.getEmail());
	        profile.setMedicalHistory(updatedProfile.getMedicalHistory());
	        profile.setUpdatedAt(LocalDateTime.now());
	        patientProfileDAO.updatePatientProfile(profile);
	    } else {
	        updatedProfile.setUser(patient);
	        updatedProfile.setCreatedAt(LocalDateTime.now());
	        updatedProfile.setUpdatedAt(LocalDateTime.now());
	        patientProfileDAO.savePatientProfile(updatedProfile);
	    }
	    return "redirect:/patient/dashboard";
	}


	@GetMapping("/notes")
	public String viewNotes(Model model, Authentication authentication) {
		String username = authentication.getName();
		User patient = userDAO.findByUsername(username);

		if (patient == null) {
			throw new RuntimeException("Patient not found");
		}

		// Fetch notes for the patient
		List<PatientNote> notes = patientNoteDAO.getNotesByPatientId(patient.getId());

		model.addAttribute("notes", notes);
		return "view-notes";
	}

	@GetMapping("/appointments")
	public String listAppointments(Authentication authentication, Model model) {
		User patient = userDAO.findByUsername(authentication.getName());
		if (patient == null || !"PATIENT".equals(patient.getRole())) {
			throw new RuntimeException("Access denied");
		}

		List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(patient.getId());
		model.addAttribute("appointments", appointments);
		return "patient-appointments"; // Template to display appointments
	}

	@GetMapping("/appointment/book")
	public String showBookingForm(Model model) {
		model.addAttribute("timeSlots", List.of("10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "01:00", "01:30",
				"02:00", "02:30", "03:00", "03:30"));
		model.addAttribute("doctors", doctorProfileDAO.getAllDoctors()); // Add list of doctors
		return "book-appointment"; // Template for booking appointments
	}

	@PostMapping("/appointment/book")
	public String bookAppointment(@RequestParam Long doctorId, @RequestParam String date, @RequestParam String time,
			Authentication authentication) {
		User patient = userDAO.findByUsername(authentication.getName());
		User doctor = userDAO.getUserById(doctorId);

		if (patient == null || !"PATIENT".equals(patient.getRole())) {
			throw new RuntimeException("Access denied");
		}

		Appointment appointment = new Appointment();
		appointment.setDoctor(doctor);
		appointment.setPatient(patient);
		appointment.setAppointmentDate(LocalDate.parse(date));
		appointment.setAppointmentTime(LocalTime.parse(time));

		appointmentDAO.saveAppointment(appointment);

		// Notify doctor about the new appointment
		Notification notification = new Notification();
		notification.setUser(doctor);
		notification.setMessage("A new appointment has been booked for " + date + " at " + time);
		notificationDAO.saveNotification(notification);

		return "redirect:/patient/appointments";
	}

	@GetMapping("/prescriptions")
	public String viewPrescriptions(Authentication authentication, Model model) {
		User patient = userDAO.findByUsername(authentication.getName());
		if (patient == null) {
			throw new RuntimeException("Patient not found");
		}

		List<Prescription> prescriptions = prescriptionDAO.getPrescriptionsByPatientId(patient.getId());
		model.addAttribute("prescriptions", prescriptions);
		return "view-prescription";
	}

	@GetMapping("/prescription/download/{id}")
	public void downloadPrescription(@PathVariable Long id, HttpServletResponse response) {
		Prescription prescription = prescriptionDAO.getPrescriptionById(id)
				.orElseThrow(() -> new RuntimeException("Prescription not found"));

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, out);
			document.open();
			document.add(new Paragraph("Prescription Details"));
			document.add(new Paragraph("Doctor: " + prescription.getDoctor().getUsername()));
			document.add(new Paragraph("Patient: " + prescription.getPatient().getUsername()));
			document.add(new Paragraph("Medication: " + prescription.getMedication()));
			document.add(new Paragraph("Dosage: " + prescription.getDosage()));
			document.add(new Paragraph("Issue Date: " + prescription.getIssueDate()));
			document.add(new Paragraph("Status: " + prescription.getStatus()));
			document.close();
		} catch (DocumentException e) {
			throw new RuntimeException("Error generating PDF: " + e.getMessage());
		}

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=prescription_" + id + ".pdf");
		try {
			response.getOutputStream().write(out.toByteArray());
			response.getOutputStream().flush();
		} catch (Exception e) {
			throw new RuntimeException("Error writing PDF to response: " + e.getMessage());
		}
	}

}
