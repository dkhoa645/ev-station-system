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
        String verifyUrl = "https://ev-station-system-production.up.railway.app/auth/verifySuccess?token=" + verificationToken;
//        String verifyUrl = "http://localhost:8080//auth/verify?token=" + verificationToken;
        String body = """
    <html>
    <body style="font-family: Arial, sans-serif; color: #333;">
        <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px; background-color: #f9f9f9;">
            <h2 style="color: #2E86C1;">Chào mừng đến với EV Charge!</h2>
            <p>Xin chào,</p>
            <p>Cảm ơn bạn đã đăng ký tài khoản. Vui lòng nhấn vào nút bên dưới để xác minh địa chỉ email của bạn:</p>
            <p style="text-align: center;">
                <a href="%s" style="
                    display: inline-block;
                    padding: 10px 20px;
                    font-size: 16px;
                    color: #fff;
                    background-color: #2E86C1;
                    text-decoration: none;
                    border-radius: 5px;
                ">Xác minh Email</a>
            </p>
            <p>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.</p>
            <hr style="border: none; border-top: 1px solid #eee;" />
            <p style="font-size: 12px; color: #999;">Đội ngũ EV Charge</p>
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

    public void sendCompanyEmail(String toEmail, String password)  {
        String subject = "Tài khoản công ty của bạn đã được tạo - EV Charge";
        String body = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333; background-color: #f4f4f4; padding: 20px;">
            <div style="
                max-width: 600px;
                margin: auto;
                padding: 20px;
                border: 1px solid #eee;
                border-radius: 10px;
                background-color: #ffffff;
            ">
                <h2 style="color: #2E86C1;">Xin chào Quý công ty,</h2>
                <p>Tài khoản của bạn đã được tạo thành công trên hệ thống <b>EV Charge</b>.</p>
                
                <p>Thông tin đăng nhập:</p>
                <ul style="background-color: #f0f8ff; padding: 15px; border-radius: 5px;">
                    <li><b>Email:</b> %s</li>
                    <li><b>Mật khẩu tạm thời:</b> %s</li>
                </ul>
                
                <p>Vui lòng đăng nhập và <b>đổi mật khẩu</b> sau lần đầu tiên.</p>
                
                <hr style="border: none; border-top: 1px solid #eee;" />
                <p style="font-size: 12px; color: #999;">Đội ngũ EV Charge</p>
            </div>
        </body>
        </html>
        """.formatted(toEmail, password);

        Email from = new Email("vietanh.hotrokekhai@gmail.com");
        Email recipient = new Email(toEmail);
        Content content = new Content("text/html",body);
        Mail mail = new Mail(from,subject,recipient,content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try{
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        }catch(IOException ex){
            throw new RuntimeException("Failed to send email",ex);
        }
    }


    public void sendDriverEmail(String toEmail, String password, String companyName) {
        String subject = "Tài khoản tài xế của bạn đã được tạo - EV Charge";
        String body = """
    <html>
    <body style="font-family: Arial, sans-serif; color: #333; background-color: #f4f4f4; padding: 20px;">
        <div style="
            max-width: 600px;
            margin: auto;
            padding: 20px;
            border: 1px solid #eee;
            border-radius: 10px;
            background-color: #ffffff;
        ">
            <h2 style="color: #2E86C1;">Xin chào Quý tài xế,</h2>
            <p>Bạn đã được công ty <b>%s</b> tạo tài khoản tài xế trên hệ thống <b>EV Charge</b>.</p>
            
            <p>Thông tin đăng nhập của bạn:</p>
            <ul style="background-color: #f0f8ff; padding: 15px; border-radius: 5px;">
                <li><b>Email:</b> %s</li>
                <li><b>Mật khẩu tạm thời:</b> %s</li>
            </ul>

            <p>Vui lòng đăng nhập vào hệ thống và <b>đổi mật khẩu</b> sau lần đầu tiên.</p>

            <hr style="border: none; border-top: 1px solid #eee;" />
            <p style="font-size: 12px; color: #999;">Đội ngũ EV Charge</p>
        </div>
    </body>
    </html>
    """.formatted(companyName, toEmail, password);

        Email from = new Email("vietanh.hotrokekhai@gmail.com");
        Email recipient = new Email(toEmail);
        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to send email", ex);
        }
    }

    public void sendForgetEmail(String toEmail, String verificationToken)  {
        String subject = "EV Charge Password Recovery";
        String resetUrl = "https://ev-station-system-production.up.railway.app/auth/verifyForget?token=" + verificationToken;
// String resetUrl = "http://localhost:8080/auth/verifyForget?token=" + verificationToken;

        String body = """
<html>
<body style="font-family: Arial, sans-serif; color: #333;">
    <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px; background-color: #f9f9f9;">
        <h2 style="color: #2E86C1;">Password Recovery</h2>
        <p>Hi,</p>
        <p>We received a request to reset the password for your EV Charge account.</p>
        <p>Please click the button below to reset your password:</p>
        <p style="text-align: center;">
            <a href="%s" style="
                display: inline-block;
                padding: 10px 20px;
                font-size: 16px;
                color: #fff;
                background-color: #2E86C1;
                text-decoration: none;
                border-radius: 5px;
            ">Reset Password</a>
        </p>
        <p>If you did not request a password reset, please ignore this email.</p>
        <hr style="border: none; border-top: 1px solid #eee;" />
        <p style="font-size: 12px; color: #999;">EV Charge Team</p>
    </div>
</body>
</html>
""".formatted(resetUrl);

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

