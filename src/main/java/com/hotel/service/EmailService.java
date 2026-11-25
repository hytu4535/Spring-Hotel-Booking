package com.hotel.service;

import com.hotel.event.BookingConfirmationEmailEvent;
import com.hotel.event.BookingStatusEmailEvent;
import com.hotel.event.WelcomeEmailEvent;

public interface EmailService {
    void sendWelcomeEmail(WelcomeEmailEvent event);

    void sendBookingConfirmationEmail(BookingConfirmationEmailEvent event);

    void sendBookingStatusEmail(BookingStatusEmailEvent event);
}
