package org.example.backend.controller;

import org.example.backend.model.entity.Author;
import org.example.backend.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthors() {
        try {
            List<Author> authors = authorService.getAllAuthors();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching authors: " + e.getMessage());
        }
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<?> getAuthorById(@PathVariable Integer authorId) {
        try {
            Author author = authorService.getAuthorById(authorId);
            return ResponseEntity.ok(author);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching author: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping
    public ResponseEntity<?> createAuthor(@RequestBody Author author) {
        try {
            Author createdAuthor = authorService.createAuthor(author);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Author created successfully with ID: " + createdAuthor.getAuthorId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating author: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/{authorId}")
    public ResponseEntity<?> updateAuthor(@PathVariable Integer authorId, @RequestBody Author author) {
        try {
            authorService.updateAuthor(authorId, author);
            return ResponseEntity.ok("Author updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating author: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/{authorId}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Integer authorId) {
        try {
            authorService.deleteAuthor(authorId);
            return ResponseEntity.ok("Author deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting author: " + e.getMessage());
        }
    }
}

