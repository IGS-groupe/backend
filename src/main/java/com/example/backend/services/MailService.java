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
    
    public void sendResetPasswordEmail(String toEmail, String userName, String resetToken) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setFrom(fromMail);
    helper.setTo(toEmail);
    helper.setSubject("Réinitialisation de votre mot de passe pour IGS");

    String resetUrl = "http://localhost:4200/account/updatePassword?token=" + resetToken; // Adjust the domain accordingly
    String content = "<h1>Bonjour " + userName + ",</h1>" +
                    "<p>Vous avez demandé la réinitialisation de votre mot de passe pour le compte associé à cet email.</p>" +
                    "<p>Veuillez cliquer sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>" +
                    "<a href='" + resetUrl + "'>Réinitialiser mon mot de passe</a>" +
                    "<p>Si vous n'avez pas demandé la réinitialisation, ignorez cet email.</p>" +
                    "<p>Cordialement,</p>" +
                    "<p>Équipe IGS</p>";

    helper.setText(content, true);
    mailSender.send(message);
    }
    public void sendStatusEmail(String toEmail, String status) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
    
        helper.setFrom(fromMail);
        helper.setTo(toEmail);
        helper.setSubject("Status de votre demande - IGS");
    
        String content = generateEmailContent(status);
        helper.setText(content, true); // true to send HTML
    
        mailSender.send(message);
    }
    
    private String generateEmailContent(String status) {
        String content = "<h1>Bonjour,</h1>";
    
        switch (status.toLowerCase()) {
            case "demande en attente":
                content += "<p>Votre demande d'analyse a été transmise et est <strong>en attente d'acceptation</strong>.</p>";
                break;
            case "resultats partiels":
                content += "<p>Les <strong>résultats partiels</strong> de votre analyse sont disponibles.</p>";
                break;
            case "echantillon rejeté":
                content += "<p>Malheureusement, votre échantillon a été <strong>rejeté</strong>.</p>";
                break;
            case "depassement de norme":
                content += "<p>Votre échantillon montre un <strong>dépassement de norme</strong>.</p>";
                break;
            case "en cours d'analyse":
                content += "<p>Votre échantillon est <strong>reçu au laboratoire</strong> et en cours d'analyse.</p>";
                break;
            case "resultats complets":
                content += "<p>Les <strong>résultats complets</strong> de votre analyse sont disponibles.</p>";
                break;
            case "non-potable":
                content += "<p>L'analyse indique que l'échantillon est <strong>non-potable</strong>.</p>";
                break;
            default:
                content += "<p>Il y a un problème inattendu avec votre échantillon. Veuillez nous contacter.</p>";
                break;
        }
    
        content += "<p>Pour plus d'informations, veuillez visiter notre portail ou nous contacter directement.</p>";
        content += "<p>Cordialement,</p>";
        content += "<p>L'équipe IGS</p>";
    
        return content;
    }
    

}
