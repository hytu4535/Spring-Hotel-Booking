package com.hotel.event;

public class BookingConfirmationEmailEvent {
    private final String recipient;
    private final String customerName;
    private final String bookingCode;
    private final String checkIn;
    private final String checkOut;
    private final String totalAmount;

    public BookingConfirmationEmailEvent(String recipient, String customerName, String bookingCode,
            String checkIn, String checkOut, String totalAmount) {
        this.recipient = recipient;
        this.customerName = customerName;
        this.bookingCode = bookingCode;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
    }

    public String getRecipient() { return recipient; }
    public String getCustomerName() { return customerName; }
    public String getBookingCode() { return bookingCode; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public String getTotalAmount() { return totalAmount; }
}
