package org.example.backend.model.entity;

import java.time.LocalDateTime;

public class ShoppingCart {

    private int cartId;
    private int userId;
    private LocalDateTime createdAt;

    public ShoppingCart() {
    }

    public ShoppingCart(int userId) {
        this.userId = userId;
    }

    public ShoppingCart(int cartId, int userId, LocalDateTime createdAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
