package com.github.vssavin.usmancore.spring6.email;

import com.github.vssavin.usmancore.email.EmailConfig;
import com.github.vssavin.usmancore.email.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link EmailService} interface.
 *
 * @author vssavin on 08.12.2023.
 */
@Service
class SimpleEmailService implements EmailService {

    private final JavaMailSender emailSender;

    private final EmailConfig emailConfig;

    public SimpleEmailService(JavaMailSender emailSender, EmailConfig emailConfig) {
        this.emailSender = emailSender;
        this.emailConfig = emailConfig;
    }

    @Override
    public void sendSimpleMessage(String destinationEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailConfig.getUserName());
        message.setTo(destinationEmail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

}
