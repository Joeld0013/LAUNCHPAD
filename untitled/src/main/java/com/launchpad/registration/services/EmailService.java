package com.launchpad.registration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendApprovalEmail(String to, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Launchpad - Registration Approved");
        message.setText(
                "Congratulations! Your startup registration has been approved.\n\n" +
                        "Login credentials:\n" +
                        "Email: " + to + "\n" +
                        "Password: " + password + "\n\n" +
                        "Please login and change your password immediately."
        );
        mailSender.send(message);
    }

    public void sendPendingEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Launchpad - Registration Received");
        message.setText("Thanks for registering. Your documents are under review. We'll notify you when approved.");
        mailSender.send(message);
    }
}
