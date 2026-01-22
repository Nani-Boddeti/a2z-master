package com.a2z.services.interfaces;

import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void sendHTMLEmail(String to, String subject, String body);

    void sendEmailWithAttachment(String to, String subject, String body) throws MessagingException, IOException;

    void sendTemplateEmail(String to, String subjectTemplatePath,
                           String bodyTemplatePath, Map<String, Object> model);
}
