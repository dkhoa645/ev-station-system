package com.group3.evproject.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    public void sendVerificationEmail(String toEmail, String verificationToken)  {
        String subject = "Verify your EV Charge account";
        String verifyUrl = "https://ev-station-system-production.up.railway.app/auth/verify?token=" + verificationToken;
//        String verifyUrl = "http://localhost:8080//auth/verify?token=" + verificationToken;
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

        Email from = new Email("vietanh.hotrokekhai@gmail.com");
        Email recipient = new Email(toEmail);
        Content content = new Content("text/html",body);
        Mail mail = new Mail(from,subject,recipient,content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request  request = new Request();

        try{
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        }catch(IOException ex){
            throw new RuntimeException("Failed to send email",ex);
        }


    }
}
