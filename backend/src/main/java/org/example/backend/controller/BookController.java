package org.example.backend.controller;

import org.example.backend.model.dto.BookCreateRequest;
import org.example.backend.model.entity.Book;
import org.example.backend.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookCreateRequest request) {
        try {
            Book createdBook = bookService.createBook(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Book created successfully with ISBN: " + createdBook.getIsbn());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating book: " + e.getMessage());
        }
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<?> updateBook(@PathVariable String isbn, @RequestBody BookCreateRequest request) {
        try {
            Book updatedBook = bookService.updateBook(isbn, request);
            return ResponseEntity.ok("Book updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating book: " + e.getMessage());
        }
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<?> getBook(@PathVariable String isbn) {
        try {
            Book book = bookService.getBookByIsbn(isbn);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }
            return ResponseEntity.ok(book);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // TODO: Add pagination
    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        try {
            List<Book> books =  bookService.getAllBooks();
            return ResponseEntity.ok(books); // return empty list if no books
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching books: " + e.getMessage());
        }
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
        try {
            bookService.deleteBook(isbn);
            return ResponseEntity.ok("Book deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting book: " + e.getMessage());
        }
    }
}
