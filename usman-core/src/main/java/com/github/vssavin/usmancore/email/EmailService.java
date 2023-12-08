package com.github.vssavin.usmancore.email;

/**
 * Main interface to send simple email message.
 *
 * @author vssavin on 08.12.2023.
 */
public interface EmailService {

    void sendSimpleMessage(String destinationEmail, String subject, String text);

}
