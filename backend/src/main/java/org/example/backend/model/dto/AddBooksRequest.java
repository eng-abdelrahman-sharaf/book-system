package org.example.backend.model.dto;

import java.util.List;

public class AddBooksRequest {
    private List<AddBookRequest> items;
    public List<AddBookRequest> getItems() { return items; }
    public void setItems(List<AddBookRequest> items) { this.items = items; }
}
