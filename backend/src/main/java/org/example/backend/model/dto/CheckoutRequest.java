package org.example.backend.model.dto;

import java.time.LocalDate;

public class CheckoutRequest {
    // optional if user already has stored card
    private String cardNumber;
    private LocalDate expirationDate;
    private String billingAddress;

    public CheckoutRequest() {}

    public CheckoutRequest(String cardNumber, LocalDate expirationDate, String billingAddress) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.billingAddress = billingAddress;
    }

    // ---------- Getters & Setters ----------

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }
}
