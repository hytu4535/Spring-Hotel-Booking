package com.hotel.event;

public class BookingStatusEmailEvent {
    private final String recipient;
    private final String customerName;
    private final String bookingCode;
    private final String status;
    private final String statusLabel;

    public BookingStatusEmailEvent(String recipient, String customerName, String bookingCode,
            String status, String statusLabel) {
        this.recipient = recipient;
        this.customerName = customerName;
        this.bookingCode = bookingCode;
        this.status = status;
        this.statusLabel = statusLabel;
    }

    public String getRecipient() { return recipient; }
    public String getCustomerName() { return customerName; }
    public String getBookingCode() { return bookingCode; }
    public String getStatus() { return status; }
    public String getStatusLabel() { return statusLabel; }
}
