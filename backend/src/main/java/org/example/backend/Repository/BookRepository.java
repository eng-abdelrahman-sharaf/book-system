package org.example.backend.Repository;

import com.sun.source.tree.BreakTree;
import org.example.backend.model.entity.Book;
import org.example.backend.model.enums.CategoryType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> bookRowMapper = (ResultSet rs, int rowNum) -> {
        Book book = new Book();
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setPublisherId(rs.getInt("publisher_id"));
        book.setPublicationYear(rs.getObject("publication_year", Integer.class));
        book.setSellingPrice(rs.getDouble("selling_price"));
        book.setCategory(CategoryType.valueOf(rs.getString("category")));
        book.setNumberOfBooks(rs.getInt("number_of_books"));
        book.setThreshold(rs.getInt("threshold"));
        return book;
    };
    public Book findByIsbn(String isbn) {
        String sql = "SELECT * FROM Books WHERE isbn = ?";
        return jdbcTemplate.queryForObject(sql, bookRowMapper, isbn);
    }
    public Book create(Book book){
        String sql = """
        insert into books (isbn, title, publisher_id, publication_year,
        selling_price, category, number_of_books, threshold)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(book.getIsbn(),
                book.getTitle(),
                book.getPublisherId(),
                book.getPublicationYear(),
                book.getSellingPrice(),
                book.getCategory().name(),
                book.getNumberOfBooks(),
                book.getThreshold());
        return findByIsbn(book.getIsbn());
    }
    public int deleteByIsbn(String isbn) {
        String sql = "DELETE FROM Books WHERE isbn = ?";
        return jdbcTemplate.update(sql, isbn);
    }

    public boolean existsByIsbn(String isbn) {
        String sql = "SELECT COUNT(*) FROM Books WHERE isbn = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, isbn);
        return count != null && count > 0;
    }

    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Books", Long.class);
    }
}
