package org.example.backend.service;

import org.example.backend.Repository.AuthorRepository;
import org.example.backend.model.entity.Author;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Integer authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + authorId));
    }

    public Author createAuthor(Author author) {
        validateAuthor(author);
        return authorRepository.create(author);
    }

    public Author updateAuthor(Integer authorId, Author author) {
        if (!authorRepository.existsById(authorId)) {
            throw new IllegalArgumentException("Author not found with ID: " + authorId);
        }
        validateAuthor(author);
        author.setAuthorId(authorId);
        return authorRepository.update(author);
    }

    public void deleteAuthor(Integer authorId) {
        authorRepository.delete(authorId);
    }

    private void validateAuthor(Author author) {
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Author name is required");
        }
        if (author.getName().length() > 100) {
            throw new IllegalArgumentException("Author name must be 100 characters or less");
        }
    }
}

