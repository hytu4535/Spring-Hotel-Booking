package com.hotel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(String recipient, String bookingCode, String details) {
        sendSimpleMail(
                recipient,
                "Xác nhận đặt phòng - " + bookingCode,
                "Đặt phòng của bạn đã được ghi nhận.\n\nMã tra cứu: " + bookingCode
                        + "\n" + (details == null ? "" : details)
        );
    }

    public void sendSimpleMail(String recipient, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (RuntimeException ex) {
            log.warn("Không thể gửi email tới {}", recipient, ex);
        }
    }
}
