package org.example.backend.model.dto;

public class CartBookPrice {
    private String isbn;
    private String title;
    private double unitPrice;
    private int quantity;
    private double totalPrice;

    public CartBookPrice(String isbn, String title, double unitPrice, int quantity, double totalPrice) {
        this.isbn = isbn;
        this.title = title;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public CartBookPrice() {
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
