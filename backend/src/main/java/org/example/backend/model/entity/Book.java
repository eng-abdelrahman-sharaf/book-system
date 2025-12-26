package org.example.backend.model.entity;

import org.example.backend.model.enums.Category;

public class Book {
    private String isbn;
    private String title;
    private Integer publisherId;
    private Integer publicationYear;
    private Double sellingPrice;
    private Category category;
    private Integer numberOfBooks;
    private Integer threshold;

    public Book() {
    }

    public Book(String isbn, String title, Integer publisherId, Integer publicationYear, Double sellingPrice, Category category, Integer numberOfBooks, Integer threshold) {
        this.isbn = isbn;
        this.title = title;
        this.publisherId = publisherId;
        this.publicationYear = publicationYear;
        this.sellingPrice = sellingPrice;
        this.category = category;
        this.numberOfBooks = numberOfBooks;
        this.threshold = threshold;
    }

    public String getTitle() {
        return title;
    }

    public Category getCategory() {
        return category;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public Integer getNumberOfBooks() {
        return numberOfBooks;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setNumberOfBooks(Integer numberOfBooks) {
        this.numberOfBooks = numberOfBooks;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
