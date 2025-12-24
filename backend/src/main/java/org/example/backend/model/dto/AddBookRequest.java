package org.example.backend.model.dto;

public class AddBookRequest {
    private String isbn;
    private int quantity;
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
