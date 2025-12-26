package org.example.backend.service;

import org.example.backend.Repository.BookRepository;
import org.example.backend.mapper.BookRequestMapper;
import org.example.backend.model.dto.BookCreateRequest;
import org.example.backend.model.entity.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookRequestMapper bookRequestMapper;

    public BookService(BookRepository bookRepository, BookRequestMapper bookRequestMapper) {
        this.bookRepository = bookRepository;
        this.bookRequestMapper = bookRequestMapper;
    }

    @Transactional
    public Book createBook(BookCreateRequest request) {
        validateCreateRequest(request);

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("Book already exists with ISBN: " + request.getIsbn());
        }

        Book book = bookRequestMapper.toNewBook(request);
        return bookRepository.create(book);
    }

    @Transactional
    public Book updateBook(String isbn, BookCreateRequest request) {
        validateIsbn(isbn);
        validateUpdateRequest(request);

        if (!bookRepository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("Book not found with ISBN: " + isbn);
        }

        Book book = bookRequestMapper.toExistingBook(isbn, request);
        return bookRepository.update(book);
    }

    public Book getBookByIsbn(String isbn) {
        validateIsbn(isbn);
        return bookRepository.read(isbn);
    }

    public List<Book> getAllBooks() {
        return bookRepository.readAll();
    }

    public List<Book> searchBooks(String title, String author, String isbn, String category, String publisher) {
        // If all filters are empty, return all books
        if ((title == null || title.isBlank()) &&
            (author == null || author.isBlank()) &&
            (isbn == null || isbn.isBlank()) &&
            (category == null || category.isBlank()) &&
            (publisher == null || publisher.isBlank())) {
            return bookRepository.readAll();
        }
        return bookRepository.searchBooks(title, author, isbn, category, publisher);
    }

    @Transactional
    public void deleteBook(String isbn) {
        validateIsbn(isbn);

        if (!bookRepository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("Book not found with ISBN: " + isbn);
        }

        bookRepository.delete(isbn);
    }

    private void validateCreateRequest(BookCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book request cannot be null");
        }

        validateIsbn(request.getIsbn());
        validateTitle(request.getTitle());
        validatePublisherId(request.getPublisherId());
        validatePrice(request.getSellingPrice());

        if (request.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }

        validateQuantities(request);
    }

    private void validateUpdateRequest(BookCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book request cannot be null");
        }

        boolean hasUpdatableField =
                request.getTitle() != null ||
                        request.getPublisherId() != null ||
                        request.getPublicationYear() != null ||
                        request.getSellingPrice() != null ||
                        request.getCategory() != null ||
                        request.getNumberOfBooks() != null ||
                        request.getThreshold() != null;

        if (!hasUpdatableField) {
            throw new IllegalArgumentException("No fields provided for update");
        }

        if (request.getTitle() != null) {
            validateTitle(request.getTitle());
        }

        if (request.getPublisherId() != null) {
            validatePublisherId(request.getPublisherId());
        }

        if (request.getSellingPrice() != null) {
            validatePrice(request.getSellingPrice());
        }

        if (request.getPublicationYear() != null && request.getPublicationYear() <= 0) {
            throw new IllegalArgumentException("Publication year must be positive");
        }

        validateQuantities(request);
    }

    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
    }

    private void validatePublisherId(Integer publisherId) {
        if (publisherId == null || publisherId <= 0) {
            throw new IllegalArgumentException("Publisher id must be positive");
        }
    }

    private void validatePrice(Double price) {
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Selling price must be positive");
        }
    }

    private void validateQuantities(BookCreateRequest request) {
        if (request.getNumberOfBooks() != null && request.getNumberOfBooks() < 0) {
            throw new IllegalArgumentException("Number of books cannot be negative");
        }

        if (request.getThreshold() != null && request.getThreshold() < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
    }
}
