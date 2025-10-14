package com.launchpad.admin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class AdminEmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String FROM_ADDRESS = "admin@launchpad.com";

    public void sendApprovalEmail(String to, String contactPerson, String startupName, String loginUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject("Your Startup Has Been Approved — Launchpad Admin");

            String html =
                    "<html><body style='background:#f8f7f2;margin:0;padding:0;'>"
                            + "<div style='max-width:600px;margin:auto;background:#fff;border-radius:10px;box-shadow:0 2px 8px #edb96f40;'>"
                            + "<div style='background:#2b3446;padding:24px 32px;border-radius:10px 10px 0 0;'>"
                            + "<h2 style='color:#edb96f;margin:0;'>Launchpad Approval Notice</h2>"
                            + "</div>"
                            + "<div style='padding:32px;'>"
                            + "<p style='color:#2b3446;margin-top:0;'>Dear " + contactPerson + ",</p>"
                            + "<p style='color:#2b3446;'>We are delighted to inform you that your startup <b>'" + startupName + "'</b> has been officially approved by Launchpad's admin team.</p>"
                            + "<div style='margin:24px 0;'>"
                            + "<a style='background:#edb96f;color:#2b3446;padding:12px 30px;border-radius:4px;font-weight:bold;text-decoration:none;' href='" + loginUrl + "'>Login to Dashboard</a>"
                            + "</div>"
                            + "<div style='background:#f8f7f2;border-left:4px solid #edb96f; padding:16px 18px;border-radius:6px;margin-bottom:18px;'>"
                            + "<strong style='color:#2b3446;'>Next Steps:</strong>"
                            + "<ul style='color:#2b3446;line-height:1.5;padding-left:18px;'>"
                            + "<li>Review and complete your profile to attract investors.</li>"
                            + "<li>Connect with Launchpad support team for any questions.</li>"
                            + "<li>Monitor new opportunities on your dashboard.</li>"
                            + "</ul>"
                            + "</div>"
                            + "<p style='color:#2b3446;'>Our support team (<a style='color:#edb96f;' href='mailto:support@launchpad.com'>support@launchpad.com</a>) is available to assist you at every stage.</p>"
                            + "<p style='font-size:12px;color:#999;margin-top:40px;'>This is an official email sent from the Launchpad Admin System.<br/>If you received this message in error, contact us immediately.</p>"
                            + "</div>"
                            + "</div>"
                            + "</body></html>";

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRejectionEmail(String to, String contactPerson, String startupName, String comments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject("Your Startup Registration Decision — Launchpad Admin");

            String html =
                    "<html><body style='background:#f8f7f2;margin:0;padding:0;'>"
                            + "<div style='max-width:600px;margin:auto;background:#fff;border-radius:10px;box-shadow:0 2px 8px #edb96f40;'>"
                            + "<div style='background:#2b3446;padding:24px 32px;border-radius:10px 10px 0 0;'>"
                            + "<h2 style='color:#edb96f;margin:0;'>Registration Decision</h2>"
                            + "</div>"
                            + "<div style='padding:32px;'>"
                            + "<p style='color:#2b3446;margin-top:0;'>Dear " + contactPerson + ",</p>"
                            + "<p style='color:#2b3446;'>We regret to inform you that your application for <b>'" + startupName + "'</b> was not approved at this time.</p>"
                            + "<div style='background:#f8f7f2;border-left:4px solid #edb96f; padding:12px 16px;border-radius:6px;margin:18px 0;'>"
                            + "<strong style='color:#2b3446;'>Reason for Rejection:</strong><br>"
                            + "<span style='color:#2b3446;'>" + (comments != null && !comments.isEmpty() ? comments : "Your application did not meet our current criteria.") + "</span>"
                            + "</div>"
                            + "<p style='color:#2b3446;'>You are welcome to revise your submission and reapply in the future. For guidance and support, please contact us at <a style='color:#edb96f;' href='mailto:support@launchpad.com'>support@launchpad.com</a>.</p>"
                            + "<p style='font-size:12px;color:#999;margin-top:40px;'>This is an official email sent from the Launchpad Admin System.<br/>If you received this message in error, contact us immediately.</p>"
                            + "</div>"
                            + "</div>"
                            + "</body></html>";

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
