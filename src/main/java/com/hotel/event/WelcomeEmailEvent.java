package com.hotel.event;

public class WelcomeEmailEvent {
    private final String recipient;
    private final String customerName;
    private final String username;

    public WelcomeEmailEvent(String recipient, String customerName, String username) {
        this.recipient = recipient;
        this.customerName = customerName;
        this.username = username;
    }

    public String getRecipient() { return recipient; }
    public String getCustomerName() { return customerName; }
    public String getUsername() { return username; }
}
