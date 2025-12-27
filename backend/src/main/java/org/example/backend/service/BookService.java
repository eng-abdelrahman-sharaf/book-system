package org.example.backend.service;

import org.example.backend.Repository.AuthorRepository;
import org.example.backend.Repository.BookRepository;
import org.example.backend.mapper.BookRequestMapper;
import org.example.backend.model.dto.BookCreateRequest;
import org.example.backend.model.entity.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookRequestMapper bookRequestMapper;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, BookRequestMapper bookRequestMapper, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookRequestMapper = bookRequestMapper;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public Book createBook(BookCreateRequest request) {
        validateCreateRequest(request);

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("Book already exists with ISBN: " + request.getIsbn());
        }

        Book book = bookRequestMapper.toNewBook(request);
        book = bookRepository.create(book);
        
        // Set author relationships
        List<Integer> authorIdsToSet = getAuthorIds(request);
        if (authorIdsToSet != null && !authorIdsToSet.isEmpty()) {
            // Validate all author IDs exist
            for (Integer authorId : authorIdsToSet) {
                if (!authorRepository.existsById(authorId)) {
                    throw new IllegalArgumentException("Author not found with ID: " + authorId);
                }
            }
            bookRepository.setBookAuthors(book.getIsbn(), authorIdsToSet);
        }
        
        return bookRepository.read(book.getIsbn());
    }

    @Transactional
    public Book updateBook(String isbn, BookCreateRequest request) {
        validateIsbn(isbn);
        validateUpdateRequest(request);

        if (!bookRepository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("Book not found with ISBN: " + isbn);
        }

        Book book = bookRequestMapper.toExistingBook(isbn, request);
        book = bookRepository.update(book);
        
        // Update author relationships if provided
        List<Integer> authorIdsToSet = getAuthorIds(request);
        if (authorIdsToSet != null) {
            // Validate all author IDs exist
            for (Integer authorId : authorIdsToSet) {
                if (!authorRepository.existsById(authorId)) {
                    throw new IllegalArgumentException("Author not found with ID: " + authorId);
                }
            }
            bookRepository.setBookAuthors(isbn, authorIdsToSet);
        }
        
        return bookRepository.read(isbn);
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

        // Delete author relationships first (though CASCADE should handle this)
        bookRepository.deleteBookAuthors(isbn);
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
        if (request.getThreshold() != null && request.getThreshold() < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        if (request.getThreshold() != null &&  request.getNumberOfBooks() != null && request.getThreshold() >  request.getNumberOfBooks()) {
            throw new IllegalArgumentException("Threshold cannot be greater than number of books");
        }
    }

    private List<Integer> getAuthorIds(BookCreateRequest request) {
        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            return request.getAuthorIds();
        }
        
        // Backward compatibility: if authorName is provided, convert to author IDs
        if (request.getAuthorName() != null && !request.getAuthorName().isBlank()) {
            String[] authorNames = request.getAuthorName().split(",");
            return authorRepository.findOrCreateByNames(
                Arrays.stream(authorNames)
                    .map(String::trim)
                    .filter(name -> !name.isBlank())
                    .collect(Collectors.toList())
            );
        }
        
        return null;
    }
}
