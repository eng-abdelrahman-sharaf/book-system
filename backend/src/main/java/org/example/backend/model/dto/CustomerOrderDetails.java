package org.example.backend.model.dto;

import org.example.backend.model.entity.CustomerOrder;
import org.example.backend.model.entity.CustomerOrderItem;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerOrderDetails {
    private int orderId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private List<BookItem> items;

    public CustomerOrderDetails() {
    }

    public CustomerOrderDetails(int orderId, LocalDateTime orderDate, double totalAmount, List<BookItem> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<BookItem> getItems() {
        return items;
    }

    public void setItems(List<BookItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

}
