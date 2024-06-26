package com.example.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.example.backend.entity.AnalysisStatus;  // Import the enum

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendActivationEmail(String toEmail, String userName, String activationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart

        helper.setFrom(fromMail);
        helper.setTo(toEmail);
        helper.setSubject("Activate your IGS account");
        String activationUrl = "http://localhost:4000/api/auth/activate?token=" + activationToken; // Adjust the domain accordingly
        String content = "<h1>Hello,</h1>" +
                        "<p>You are receiving this email because <strong>" + userName + "</strong> has granted you access to the IGS web portal on behalf of Nyrstar Bouchard Hébert.</p>" +
                        "<p>We invite you to activate your user account to access the IGS web portal. This free service allows you to plan, record, and track your samples in real time. Additionally, you have access to the archival data of previous samples.</p>" +
                        "<p>Simply <a href='" + activationUrl + "'>click on this link</a> to activate your access:</p>" +
                        "<p><b>Activate Account</b></p>" +
                        "<p>If you have questions about your new account, please feel free to contact us.</p>" +
                        "<p>Kind regards,</p>" +
                        "<p>IGS</p>";

        helper.setText(content, true); // true to send HTML

        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String toEmail, String userName, String resetToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromMail);
        helper.setTo(toEmail);
        helper.setSubject("Reset your password for IGS");

        String resetUrl = "http://localhost:4200/account/updatePassword?token=" + resetToken; // Adjust the domain accordingly
        String content = "<h1>Hello " + userName + ",</h1>" +
                        "<p>You have requested a reset of your password for the account associated with this email.</p>" +
                        "<p>Please click on the link below to reset your password:</p>" +
                        "<a href='" + resetUrl + "'>Reset my password</a>" +
                        "<p>If you did not request a reset, please ignore this email.</p>" +
                        "<p>Kind regards,</p>" +
                        "<p>IGS Team</p>";

        helper.setText(content, true);
        mailSender.send(message);
    }

    public void sendStatusEmail(String toEmail, AnalysisStatus status) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromMail);
        helper.setTo(toEmail);
        helper.setSubject("Status of your request - IGS");

        String content = "<h1>Hello,</h1>" +
                         "<p>" + status.getDescription() + " " + status.getSymbol() + "</p>" +
                         "<p>For more information, please visit our portal or contact us directly.</p>" +
                         "<p>Kind regards,</p>" +
                         "<p>IGS Team</p>";

        helper.setText(content, true); // true to send HTML

        mailSender.send(message);
    }
}
