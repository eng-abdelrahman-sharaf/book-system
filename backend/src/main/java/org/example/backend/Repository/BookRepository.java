package org.example.backend.Repository;

import org.example.backend.model.entity.Book;
import org.example.backend.model.enums.CategoryType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

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
    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM Books", bookRowMapper);
    }
    public List<Book> findByCategory(CategoryType category) {
        String sql = "SELECT * FROM Books WHERE category = ?";
        return jdbcTemplate.query(sql, bookRowMapper, category.name());
    }

    public List<Book> findByPublisher(int publisherId) {
        String sql = "SELECT * FROM Books WHERE publisher_id = ?";
        return jdbcTemplate.query(sql, bookRowMapper, publisherId);
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
    public void deductStock(String isbn, int quantity) {
        String sql = "UPDATE Books SET number_of_books = number_of_books - ? WHERE isbn = ?";
        jdbcTemplate.update(sql, quantity, isbn);
    }
}
