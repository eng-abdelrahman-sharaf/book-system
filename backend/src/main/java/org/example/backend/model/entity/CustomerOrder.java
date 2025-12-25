package org.example.backend.model.entity;

import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;

public class CustomerOrder {

    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus status;

    public CustomerOrder() {}

    public CustomerOrder(int orderId, int userId,
                         LocalDateTime orderDate,
                         double totalAmount,
                         OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}