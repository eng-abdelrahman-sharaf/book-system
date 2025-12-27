package org.example.backend.mapper;

import org.example.backend.model.entity.Book;
import org.example.backend.model.enums.Category;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book result = new Book();
        result.setIsbn(rs.getString("isbn"));
        result.setTitle(rs.getString("title"));
        result.setPublisherId(rs.getObject("publisher_id", Integer.class));
        result.setPublicationYear(rs.getObject("publication_year", Integer.class));

        BigDecimal price = rs.getBigDecimal("selling_price");
        result.setSellingPrice(price != null ? price.doubleValue() : null);

        String category = rs.getString("category");
        if (category != null) {
            result.setCategory(Category.valueOf(category));
        }

        result.setNumberOfBooks(rs.getObject("number_of_books", Integer.class));
        result.setThreshold(rs.getObject("threshold", Integer.class));
            result.setPublisherName(rs.getString("publisher_name"));
            result.setAuthorName(rs.getString("author_name"));
        return result;
    }
}
