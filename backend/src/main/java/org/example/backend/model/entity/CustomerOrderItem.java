package org.example.backend.model.entity;

public class CustomerOrderItem {
    private int orderId;
    private String isbn;
    private int quantity;
    private double price;

    public CustomerOrderItem() {}

    public CustomerOrderItem(int orderId, String isbn,
                             int quantity, double price) {
        this.orderId = orderId;
        this.isbn = isbn;
        this.quantity = quantity;
        this.price = price;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
