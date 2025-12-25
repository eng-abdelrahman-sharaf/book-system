package org.example.backend.model.dto;

import org.example.backend.model.entity.CustomerOrder;
import org.example.backend.model.entity.CustomerOrderItem;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerOrderDetails {
    private int orderId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private List<CustomerOrderItem> items;
    private String title;

    public CustomerOrderDetails() {
    }

    public CustomerOrderDetails(int orderId, LocalDateTime orderDate, double totalAmount, List<CustomerOrderItem> items, String title) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<CustomerOrderItem> getItems() {
        return items;
    }

    public void setItems(List<CustomerOrderItem> items) {
        this.items = items;
    }
}
