package com.group3.evproject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationToken) {
        String subject = "Email Verification";
        String verifyUrl = "http://localhost:8080/auth/verify?token=" + verificationToken;

        // Nội dung email dạng HTML
        String body = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px; background-color: #f9f9f9;">
                <h2 style="color: #2E86C1;">Welcome to EV Charge!</h2>
                <p>Hi,</p>
                <p>Thank you for registering. Please click the button below to verify your email address:</p>
                <p style="text-align: center;">
                    <a href="%s" style="
                        display: inline-block;
                        padding: 10px 20px;
                        font-size: 16px;
                        color: #fff;
                        background-color: #2E86C1;
                        text-decoration: none;
                        border-radius: 5px;
                    ">Verify Email</a>
                </p>
                <p>If you did not register, please ignore this email.</p>
                <hr style="border: none; border-top: 1px solid #eee;" />
                <p style="font-size: 12px; color: #999;">EV Charge Team</p>
            </div>
        </body>
        </html>
        """.formatted(verifyUrl);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("vietanh.hotrokekhai@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
