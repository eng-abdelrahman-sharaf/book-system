package org.example.backend.model.entity;

public class ShoppingCartItem {
    private int cartId;
    private String isbn;
    private int quantity;

    public ShoppingCartItem() {
    }

    public ShoppingCartItem(int cartId, String isbn, int quantity) {
        this.cartId = cartId;
        this.isbn = isbn;
        this.quantity = quantity;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
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
}
