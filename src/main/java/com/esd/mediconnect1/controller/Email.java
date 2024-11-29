package com.esd.mediconnect1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Email {

	@Autowired
	private JavaMailSender mailSender;
//	
	public void sendEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("${spring.mail.username}"); // Add this line
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
		} catch (MailException e) {
			// Log the error or handle it appropriately
			throw new RuntimeException("Failed to send email: " + e.getMessage());
		}
	}
	
	
}
