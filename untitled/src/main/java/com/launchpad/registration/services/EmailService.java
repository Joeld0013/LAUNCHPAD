package com.launchpad.registration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String FROM_ADDRESS = "admin@launchpad.com";

    public void sendPendingEmail(String to, String name, String startupName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject("Launchpad Registration Received");

            String html =
                    "<html><body style='background:#f8f7f2; margin:0; padding:0;'>"
                            + "<div style='max-width:600px;margin:auto;background:#fff;border-radius:10px;box-shadow:0 2px 8px #edb96f40;'>"
                            + "<div style='background:#edb96f;padding:24px 32px;border-radius:10px 10px 0 0;'>"
                            + "<h2 style='color:#2b3446;margin:0;'>Registration Received</h2>"
                            + "</div>"
                            + "<div style='padding:32px;'>"
                            + "<p style='color:#2b3446;'>Dear " + name + ",</p>"
                            + "<p style='color:#2b3446;'>Thank you for registering <strong>'" + startupName + "'</strong> on Launchpad.</p>"
                            + "<p style='color:#2b3446;'>Our team is reviewing your submission. You’ll be notified when approved or if additional details are needed.</p>"
                            + "<p style='color:#2b3446;'>Questions? Email <a style='color:#edb96f;' href='mailto:support@launchpad.com'>support@launchpad.com</a>.</p>"
                            + "<p style='color:#999;font-size:12px;'>This is an automated message from Launchpad.</p>"
                            + "</div>"
                            + "</div>"
                            + "</body></html>";
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendApprovalEmail(String to, String name, String startupName, String loginUrl, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject("Your Startup Registration is Approved — Launchpad");

            String html =
                    "<html><body style='background:#f8f7f2; margin:0; padding:0;'>"
                            + "<div style='max-width:600px;margin:auto;background:#fff;border-radius:10px;box-shadow:0 2px 8px #edb96f40;'>"
                            + "<div style='background:#edb96f;padding:24px 32px;border-radius:10px 10px 0 0;'>"
                            + "<h2 style='color:#2b3446;margin:0;'>Congratulations!</h2>"
                            + "</div>"
                            + "<div style='padding:32px;'>"
                            + "<p style='color:#2b3446;'>Dear " + name + ",</p>"
                            + "<p style='color:#2b3446;'>We are pleased to inform you your startup <b>'" + startupName +
                            "'</b> has been approved on <b>Launchpad</b>!</p>"
                            + "<div style='margin:20px 0;'>"
                            + "<a style='background:#edb96f;color:#2b3446;padding:12px 30px;border-radius:4px;text-decoration:none;font-weight:bold;' href='" + loginUrl + "'>Login to Dashboard</a>"
                            + "</div>"
                            + "<div style='background:#f8f7f2;border-left:4px solid #edb96f; padding:12px 16px;border-radius:6px;'>"
                            + "<strong style='color:#2b3446;'>Login Credentials:</strong><br>"
                            + "Email: " + to + "<br>"
                            + "Password: " + password + "<br>"
                            + "<span style='font-size:12px;color:#999;'>Please change your password after logging in for security.</span>"
                            + "</div>"
                            + "<p style='color:#2b3446;'>Your profile is now live. You can manage details, connect with investors, and access resources on your dashboard.</p>"
                            + "<p style='color:#2b3446;'>Questions? Email <a style='color:#edb96f;' href='mailto:support@launchpad.com'>support@launchpad.com</a>.</p>"
                            + "<p style='color:#999;font-size:12px;'>This is an automated message from Launchpad.</p>"
                            + "</div>"
                            + "</div>"
                            + "</body></html>";
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRejectedEmail(String to, String name, String startupName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject("Your Startup Registration Decision — Launchpad");

            String html =
                    "<html><body style='background:#f8f7f2; margin:0; padding:0;'>"
                            + "<div style='max-width:600px;margin:auto;background:#fff;border-radius:10px;box-shadow:0 2px 8px #edb96f40;'>"
                            + "<div style='background:#2b3446;padding:24px 32px;border-radius:10px 10px 0 0;'>"
                            + "<h2 style='color:#edb96f;margin:0;'>Registration Decision</h2>"
                            + "</div>"
                            + "<div style='padding:32px;'>"
                            + "<p style='color:#2b3446;'>Dear " + name + ",</p>"
                            + "<p style='color:#2b3446;'>We regret to inform you your application for <strong>'" + startupName + "'</strong> was not approved.</p>"

                            + "<p style='color:#2b3446;'>You may revise your submission and reapply in future. Contact <a style='color:#edb96f;' href='mailto:support@launchpad.com'>support@launchpad.com</a> for guidance.</p>"
                            + "<p style='color:#999;font-size:12px;'>This is an automated message from Launchpad.</p>"
                            + "</div>"
                            + "</div>"
                            + "</body></html>";
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // These following methods should delegate to the ones above! Otherwise, they WILL NOT send an email!

    // Used in approveStartup (with password) -- DELEGATE to main function
    public void sendApprovalEmail(String email, String rawPassword) {
        // If you need to add more info, pass it as params; otherwise you must have access to name and startupName here
        // For example purpose, call as:
        // sendApprovalEmail(email, name, startupName, loginUrl, rawPassword);
        // If name/startupName/loginUrl not available here, refactor your code to pass them in
        // Example:
        sendApprovalEmail(email, email, email, "http://localhost:8080/startup_login.html", rawPassword);
    }

    public void sendPendingEmail(String email) {
        // As above, if you have name and startupName pass them in, otherwise pass email as both!
        sendPendingEmail(email, email, email);
    }

    public void sendApprovedEmail(String email, String name) {
        // This can use a basic template or call the more detailed version, here is an example:
        sendApprovalEmail(email, name, name, "http://localhost:8080/startup_login.html", "********");
    }
}
