package org.example.backend.model.dto;

import org.example.backend.model.entity.Book;

public class BookOrderHistory {
    private Book book;
    private int numberoforders;

    public int getNumberoforders() {
        return numberoforders;
    }
    public void setNumberoforders(int numberoforders) {
        this.numberoforders = numberoforders;
    }
    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }
}
