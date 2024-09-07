package com.natu.ftax.transaction.infrastructure;

import com.natu.ftax.common.exception.TechnicalException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${base.url}")
    private String baseUrl;

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    public EmailService(SpringTemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public void sendMagicLink(String toEmail, String username, String hash) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("hash", hash);
        context.setVariable("email", toEmail);

        String body = templateEngine.process("magic-link", context);

        sendEmail(toEmail, "Ftax: %s - Your magic link".formatted(username), body);
    }


    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            // Set the email content as HTML
            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Error while sending email: {}", e.getMessage());
            throw new TechnicalException("Error while sending email: {0}", e.getMessage());
        }
    }
}
