package com.hotel.service.impl;

import com.hotel.event.BookingConfirmationEmailEvent;
import com.hotel.event.BookingStatusEmailEvent;
import com.hotel.event.WelcomeEmailEvent;
import com.hotel.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String from;
    private final String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine,
            @Value("${app.mail.from}") String from,
            @Value("${app.frontend-url:http://localhost:8080}") String frontendUrl) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    @Override
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendWelcomeEmail(WelcomeEmailEvent event) {
        Context context = new Context();
        context.setVariable("customerName", event.getCustomerName());
        context.setVariable("username", event.getUsername());
        context.setVariable("frontendUrl", frontendUrl);
        sendTemplate(event.getRecipient(), "Chào mừng bạn đến với Hotel Booking",
                "email/welcome", context);
    }

    @Override
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendBookingConfirmationEmail(BookingConfirmationEmailEvent event) {
        Context context = new Context();
        context.setVariable("customerName", event.getCustomerName());
        context.setVariable("bookingCode", event.getBookingCode());
        context.setVariable("checkIn", event.getCheckIn());
        context.setVariable("checkOut", event.getCheckOut());
        context.setVariable("totalAmount", event.getTotalAmount());
        context.setVariable("frontendUrl", frontendUrl);
        sendTemplate(event.getRecipient(), "Xác nhận đặt phòng - " + event.getBookingCode(),
                "email/booking-confirmation", context);
    }

    @Override
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendBookingStatusEmail(BookingStatusEmailEvent event) {
        Context context = new Context();
        context.setVariable("customerName", event.getCustomerName());
        context.setVariable("bookingCode", event.getBookingCode());
        context.setVariable("status", event.getStatus());
        context.setVariable("statusLabel", event.getStatusLabel());
        context.setVariable("frontendUrl", frontendUrl);
        sendTemplate(event.getRecipient(), "Cập nhật trạng thái đơn - " + event.getBookingCode(),
                "email/booking-status", context);
    }

    private void sendTemplate(String recipient, String subject, String template, Context context) {
        if (recipient == null || recipient.trim().isEmpty()) {
            log.warn("Bỏ qua email {} vì người nhận trống.", subject);
            return;
        }
        try {
            String html = templateEngine.process(template, context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(recipient.trim());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | MailException ex) {
            log.warn("Không thể gửi email '{}' tới {}.", subject, recipient, ex);
        } catch (RuntimeException ex) {
            log.warn("Không thể render hoặc gửi email '{}' tới {}.", subject, recipient, ex);
        }
    }
}
