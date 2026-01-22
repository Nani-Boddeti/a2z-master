package com.a2z.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import com.a2z.services.interfaces.EmailService;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class DefaultEmailService  implements EmailService {

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    @Override
    public void sendHTMLEmail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom(new InternetAddress("sender@example.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);

            String htmlContent = "<h1>This is a test Spring Boot email</h1>" +
                    "<p>It can contain <strong>HTML</strong> content.</p>";
            message.setContent(htmlContent, "text/html; charset=utf-8");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mailSender.send(message);
    }
    @Override
    public void sendEmailWithAttachment(String to, String subject, String body) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        FileSystemResource file = new FileSystemResource(new File("attachment.jpg"));
        helper.addAttachment("attachment.jpg", file);
        mailSender.send(message);
    }

    @Override
    public void sendTemplateEmail(String to, String subjectTemplatePath,
                                  String bodyTemplatePath, Map<String, Object> model) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);

            // Render subject from VM template
            Template subjectTemplate = velocityEngine.getTemplate(subjectTemplatePath);
            StringWriter subjectWriter = new StringWriter();
            subjectTemplate.merge(new VelocityContext(model), subjectWriter);
            helper.setSubject(subjectWriter.toString());

            // Render body from VM template (HTML)
            Template bodyTemplate = velocityEngine.getTemplate(bodyTemplatePath);
            StringWriter bodyWriter = new StringWriter();
            bodyTemplate.merge(new VelocityContext(model), bodyWriter);
            helper.setText(bodyWriter.toString(), true); // true = HTML

            mailSender.send(message);
        } catch (Exception e) {
            // Handle exception
        }
    }
}
