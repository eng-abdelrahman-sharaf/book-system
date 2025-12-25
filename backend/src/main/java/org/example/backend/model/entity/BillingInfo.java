package org.example.backend.model.entity;

import java.time.LocalDate;

public class BillingInfo {

    private int userId;
    private String cardNumber;
    private LocalDate expirationDate;
    private String billingAddress;

    public BillingInfo() {}

    public BillingInfo(int userId, String cardNumber, LocalDate expirationDate, String billingAddress) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.billingAddress = billingAddress;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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
