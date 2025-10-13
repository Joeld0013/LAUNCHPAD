package com.launchpad.admin.services;

import com.launchpad.admin.model.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AdminEmailService {

    private static final Logger logger = LoggerFactory.getLogger(AdminEmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Send approval email to startup
     */
    public void sendApprovalEmail(Startup startup) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(startup.getEmail());
            helper.setSubject("🎉 Congratulations! Your Startup Registration is Approved");

            String htmlContent = buildApprovalEmailContent(startup);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Approval email sent successfully to: {}", startup.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send approval email to: {}", startup.getEmail(), e);
            throw new RuntimeException("Failed to send approval email", e);
        }
    }

    public void sendApprovedEmail(String to, String name) {
        String subject = "Your Startup Is Approved!";
        String body = "Dear " + name + ",\nYour registration is approved. You can now login.";
        sendSimpleEmail(to, subject, body);
    }

    public void sendRejectedEmail(String to, String name, String comments) {
        String subject = "Your Startup Registration Was Rejected";
        String body = "Dear " + name + ",\nYour application was rejected. Reason: " + comments;
        sendSimpleEmail(to, subject, body);
    }

    private void sendSimpleEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            mailSender.send(message);
            logger.info("Email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
        }
    }

    /**
     * Send rejection email to startup
     */
    public void sendRejectionEmail(Startup startup, String comments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(startup.getEmail());
            helper.setSubject("Update on Your Startup Registration Application");

            String htmlContent = buildRejectionEmailContent(startup, comments);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Rejection email sent successfully to: {}", startup.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send rejection email to: {}", startup.getEmail(), e);
            throw new RuntimeException("Failed to send rejection email", e);
        }
    }

    /**
     * Send under review notification
     */
    public void sendUnderReviewEmail(Startup startup) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(startup.getEmail());
            helper.setSubject("Your Startup Application is Under Review");

            String htmlContent = buildUnderReviewEmailContent(startup);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Under review email sent successfully to: {}", startup.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send under review email to: {}", startup.getEmail(), e);
            throw new RuntimeException("Failed to send under review email", e);
        }
    }

    /**
     * Build HTML content for approval email
     */
    private String buildApprovalEmailContent(Startup startup) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String approvalDate = startup.getApprovedAt() != null
                ? dateFormat.format(startup.getApprovedAt())
                : dateFormat.format(new Date());

        String contactPerson = startup.getContactPerson() != null ? startup.getContactPerson() : "Founder";

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".header h1 { margin: 0; font-size: 28px; }" +
                ".content { background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".info-box { background: #f0f4ff; border-left: 4px solid #667eea; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                ".info-box strong { color: #667eea; }" +
                ".button { display: inline-block; background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold; }" +
                ".button:hover { background: #5568d3; }" +
                ".footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }" +
                ".success-icon { font-size: 50px; margin-bottom: 10px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<div class='success-icon'>✅</div>" +
                "<h1>Congratulations!</h1>" +
                "<p>Your startup has been approved</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Dear " + contactPerson + ",</p>" +
                "<p>We are excited to inform you that <strong>" + startup.getName() + "</strong> has been successfully approved and verified on our LaunchPad platform!</p>" +
                "<div class='info-box'>" +
                "<strong>Approval Details:</strong><br>" +
                "Startup Name: " + startup.getName() + "<br>" +
                "Industry: " + (startup.getIndustry() != null ? startup.getIndustry() : "N/A") + "<br>" +
                "Approval Date: " + approvalDate + "<br>" +
                "Status: <span style='color: #28a745; font-weight: bold;'>APPROVED & VERIFIED ✓</span>" +
                "</div>" +
                "<h3>What's Next?</h3>" +
                "<ul>" +
                "<li>✓ Your startup profile is now live and visible to investors</li>" +
                "<li>✓ You can log in to your dashboard using your registered email and password</li>" +
                "<li>✓ Complete your profile with additional details to attract more investors</li>" +
                "<li>✓ Start connecting with potential investors in your industry</li>" +
                "<li>✓ Access our resources and tools for startup growth</li>" +
                "</ul>" +
                "<center>" +
                "<a href='" + frontendUrl + "/login' class='button'>Login to Your Dashboard</a>" +
                "</center>" +
                "<p><strong>Login Credentials:</strong></p>" +
                "<div class='info-box'>" +
                "Email: " + startup.getEmail() + "<br>" +
                "Password: Use the password you created during registration<br>" +
                "<small style='color: #666;'>If you forgot your password, use the 'Forgot Password' option on the login page.</small>" +
                "</div>" +
                "<h3>Need Help?</h3>" +
                "<p>Our support team is here to help you succeed. If you have any questions or need assistance, please don't hesitate to reach out:</p>" +
                "<ul>" +
                "<li>Email: support@launchpad.com</li>" +
                "<li>Phone: +1 (555) 123-4567</li>" +
                "<li>Help Center: " + frontendUrl + "/help</li>" +
                "</ul>" +
                "<p>We're thrilled to have " + startup.getName() + " as part of our community and look forward to supporting your journey to success!</p>" +
                "<p>Best regards,<br><strong>The LaunchPad Team</strong></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>© 2025 LaunchPad. All rights reserved.</p>" +
                "<p>This is an automated message. Please do not reply to this email.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Build HTML content for rejection email
     */
    private String buildRejectionEmailContent(Startup startup, String comments) {
        String contactPerson = startup.getContactPerson() != null ? startup.getContactPerson() : "Founder";

        String reasonSection = "";
        if (comments != null && !comments.isEmpty()) {
            reasonSection = "<div class='info-box' style='border-left-color: #dc3545;'>" +
                    "<strong>Feedback:</strong><br>" +
                    comments +
                    "</div>";
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".header h1 { margin: 0; font-size: 28px; }" +
                ".content { background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".info-box { background: #fff5f5; border-left: 4px solid #dc3545; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                ".info-box strong { color: #dc3545; }" +
                ".button { display: inline-block; background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold; }" +
                ".button:hover { background: #5568d3; }" +
                ".footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Application Update</h1>" +
                "<p>Regarding your startup registration</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Dear " + contactPerson + ",</p>" +
                "<p>Thank you for your interest in joining the LaunchPad platform with <strong>" + startup.getName() + "</strong>.</p>" +
                "<p>After careful review of your application, we regret to inform you that we are unable to approve your startup registration at this time.</p>" +
                reasonSection +
                "<h3>What You Can Do:</h3>" +
                "<ul>" +
                "<li>Review the feedback provided above</li>" +
                "<li>Make necessary improvements to your business plan and documentation</li>" +
                "<li>Reapply after addressing the concerns mentioned</li>" +
                "<li>Contact our support team for guidance on reapplication</li>" +
                "</ul>" +
                "<p>We encourage you to reapply once you've addressed the feedback. Our platform is always open to innovative startups, and we'd love to see you succeed.</p>" +
                "<center>" +
                "<a href='" + frontendUrl + "/register' class='button'>Reapply Now</a>" +
                "</center>" +
                "<h3>Need Assistance?</h3>" +
                "<p>Our team is here to help you understand the requirements better:</p>" +
                "<ul>" +
                "<li>Email: support@launchpad.com</li>" +
                "<li>Phone: +1 (555) 123-4567</li>" +
                "</ul>" +
                "<p>We appreciate your understanding and wish you the best in your entrepreneurial journey.</p>" +
                "<p>Best regards,<br><strong>The LaunchPad Team</strong></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>© 2025 LaunchPad. All rights reserved.</p>" +
                "<p>This is an automated message. Please do not reply to this email.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Build HTML content for under review email
     */
    private String buildUnderReviewEmailContent(Startup startup) {
        String contactPerson = startup.getContactPerson() != null ? startup.getContactPerson() : "Founder";

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".header h1 { margin: 0; font-size: 28px; }" +
                ".content { background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".info-box { background: #fff9e6; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                ".footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Application Under Review</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Dear " + contactPerson + ",</p>" +
                "<p>Thank you for registering <strong>" + startup.getName() + "</strong> on LaunchPad!</p>" +
                "<div class='info-box'>" +
                "Your application is currently under review by our team. We'll notify you once the review is complete." +
                "</div>" +
                "<p>Review typically takes 2-3 business days. We appreciate your patience!</p>" +
                "<p>Best regards,<br><strong>The LaunchPad Team</strong></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>© 2025 LaunchPad. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}