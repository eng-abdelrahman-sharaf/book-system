package org.example.backend.Repository;

import org.example.backend.mapper.BookRowMapper;
import org.example.backend.model.entity.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;
    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Book create(Book book) {
        String query = "INSERT INTO books (isbn, title, publisher_id, publication_year, selling_price, category, number_of_books, threshold) VALUES (?, ?, ?, ?, ?, ?::category_type, ?, ?)";

        Integer numberOfBooks = book.getNumberOfBooks() != null ? book.getNumberOfBooks() : 0;
        Integer threshold = book.getThreshold() != null ? book.getThreshold() : 5;

        book.setNumberOfBooks(numberOfBooks);
        book.setThreshold(threshold);

        jdbcTemplate.update(
                query,
                book.getIsbn(),
                book.getTitle(),
                book.getPublisherId(),
                book.getPublicationYear(),           // nullable is ok
                book.getSellingPrice(),
            book.getCategory().name(),           // enum to string
            numberOfBooks,
            threshold
        );
        return book;
    }

    public Book update(Book book) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE books SET ");
        List<Object> params = new ArrayList<>();
        
        boolean first = true;
        if (book.getTitle() != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("title = ?");
            params.add(book.getTitle());
            first = false;
        }
        if (book.getPublisherId() != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("publisher_id = ?");
            params.add(book.getPublisherId());
            first = false;
        }
        if (book.getPublicationYear() != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("publication_year = ?");
            params.add(book.getPublicationYear());
            first = false;
        }
        if (book.getSellingPrice() != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("selling_price = ?");
            params.add(book.getSellingPrice());
            first = false;
        }
        if (book.getCategory() != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("category = ?::category_type");
            params.add(book.getCategory().name());
            first = false;
        }
        if (book.getNumberOfBooks() != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("number_of_books = ?");
            params.add(book.getNumberOfBooks());
            first = false;
        }
        if (book.getThreshold() != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("threshold = ?");
            params.add(book.getThreshold());
            first = false;
        }
        
        if (params.isEmpty()) {
            throw new IllegalArgumentException("No fields provided for update");
        }

        queryBuilder.append(" WHERE isbn = ?");
        params.add(book.getIsbn());

        jdbcTemplate.update(queryBuilder.toString(), params.toArray());
        return book;
    }

    public void delete(String isbn) {
        String query = "DELETE FROM books WHERE isbn = ?";
        this.jdbcTemplate.update(query, isbn);
    }

    public List<Book> readAll() {
        String query =
                "SELECT b.isbn, b.title, b.publisher_id, b.publication_year, b.selling_price, b.category, " +
                        "b.number_of_books, b.threshold, p.name AS publisher_name, " +
                        "NULLIF(string_agg(DISTINCT a.name, ', '), '') AS author_name " +
                        "FROM books b " +
                        "LEFT JOIN publishers p ON b.publisher_id = p.publisher_id " +
                        "LEFT JOIN bookauthors ba ON b.isbn = ba.isbn " +
                        "LEFT JOIN authors a ON ba.author_id = a.author_id " +
                        "GROUP BY b.isbn, b.title, b.publisher_id, b.publication_year, b.selling_price, b.category, " +
                        "b.number_of_books, b.threshold, p.name";

        List<Book> books = this.jdbcTemplate.query(query, new BookRowMapper());
        return books;
    }

    public Book read(String isbn) {
        String query =
                "SELECT b.isbn, b.title, b.publisher_id, b.publication_year, b.selling_price, b.category, " +
                        "b.number_of_books, b.threshold, p.name AS publisher_name, " +
                        "NULLIF(string_agg(DISTINCT a.name, ', '), '') AS author_name " +
                        "FROM books b " +
                        "LEFT JOIN publishers p ON b.publisher_id = p.publisher_id " +
                        "LEFT JOIN bookauthors ba ON b.isbn = ba.isbn " +
                        "LEFT JOIN authors a ON ba.author_id = a.author_id " +
                        "WHERE b.isbn = ? " +
                        "GROUP BY b.isbn, b.title, b.publisher_id, b.publication_year, b.selling_price, b.category, " +
                        "b.number_of_books, b.threshold, p.name";

        List<Book> books = this.jdbcTemplate.query(query, new BookRowMapper(), isbn);
        return books.stream().findFirst().orElse(null);
    }

    public boolean existsByIsbn(String isbn) {
        String query = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, isbn);
        return count != null && count > 0;
    }

    public List<Book> searchBooks(String title, String author, String isbn, String category, String publisher) {
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT b.isbn, b.title, b.publisher_id, b.publication_year, b.selling_price, b.category, " +
                    "b.number_of_books, b.threshold, p.name AS publisher_name, " +
                    "NULLIF(string_agg(DISTINCT a.name, ', '), '') AS author_name " +
                    "FROM books b " +
                    "LEFT JOIN bookauthors ba ON b.isbn = ba.isbn " +
                    "LEFT JOIN authors a ON ba.author_id = a.author_id " +
                    "LEFT JOIN publishers p ON b.publisher_id = p.publisher_id " +
                    "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            queryBuilder.append(" AND b.title ILIKE ?");
            params.add("%" + title + "%");
        }
        if (author != null && !author.isBlank()) {
            queryBuilder.append(" AND a.name ILIKE ?");
            params.add("%" + author + "%");
        }
        if (isbn != null && !isbn.isBlank()) {
            queryBuilder.append(" AND b.isbn = ?");
            params.add(isbn);
        }
        if (category != null && !category.isBlank()) {
            queryBuilder.append(" AND b.category::text = ?");
            params.add(category);
        }
        if (publisher != null && !publisher.isBlank()) {
            queryBuilder.append(" AND p.name ILIKE ?");
            params.add("%" + publisher + "%");
        }

        queryBuilder.append(" GROUP BY b.isbn, b.title, b.publisher_id, b.publication_year, b.selling_price, b.category, b.number_of_books, b.threshold, p.name");

        return jdbcTemplate.query(queryBuilder.toString(), new BookRowMapper(), params.toArray());
    }

  public void deductStock(String isbn, int quantity) {
        String sql = "UPDATE Books SET number_of_books = number_of_books - ? WHERE isbn = ?";
        jdbcTemplate.update(sql, quantity, isbn);
    }

}
