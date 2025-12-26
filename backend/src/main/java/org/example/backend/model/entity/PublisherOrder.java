package org.example.backend.model.entity;

import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;

public class PublisherOrder {
    private int orderId;
    private String isbn;
    private int quantity;
    private LocalDateTime orderDate;
    private OrderStatus status;

    public PublisherOrder() {}

    public PublisherOrder(int orderId, String isbn, int quantity, LocalDateTime orderDate, OrderStatus status) {
        this.orderId = orderId;
        this.isbn = isbn;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.status = status;
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}

