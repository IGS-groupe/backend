package com.example.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException; // Correct import
import jakarta.mail.internet.MimeMessage; // Correct import

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendActivationEmail(String toEmail, String userName ,String activationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart

        helper.setFrom(fromMail);
        helper.setTo(toEmail);
        helper.setSubject("Activation de votre compte IGS");
        String activationUrl = "http://localhost:4000/api/auth/activate?token=" + activationToken; // Adjust the domain accordingly
        String content = "<h1>Bonjour,</h1>" +
                        "<p>Vous recevez ce courriel puisque <strong>" + userName + "</strong> vous a attribué un accès au portail web IGS pour le compte de Nyrstar Bouchard Hébert.</p>" +
                        "<p>Nous vous invitons à activer votre compte d’utilisateur pour pouvoir accéder au portail web IGS. Ce service gratuit permet de planifier, d’enregistrer et d’effectuer le suivi de vos échantillons en temps réel. De plus, vous avez accès à l’archivage des données des échantillons précédents.</p>" +
                        "<p>Vous n’avez qu’à <a href='" + activationUrl + "'>cliquer sur ce lien</a> pour activer votre accès :</p>" +
                        "<p><b>Activer le compte</b></p>" +
                        "<p>Si vous avez des questions à propos de votre nouveau compte, n’hésitez pas à communiquer avec nous.</p>" +
                        "<p>Cordialement,</p>" +
                        "<p>IGS</p>";

        helper.setText(content, true); // true to send HTML

        mailSender.send(message);
    }
}
