package org.example.backend.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AuthorRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuthorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Integer> findByName(String name) {
        String query = "SELECT author_id FROM Authors WHERE name = ?";
        List<Integer> results = jdbcTemplate.query(
            query,
            (rs, rowNum) -> rs.getInt("author_id"),
            name
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Integer findOrCreateByName(String name) {
        Optional<Integer> existing = findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        String query = "INSERT INTO Authors (name) VALUES (?) RETURNING author_id";
        return jdbcTemplate.queryForObject(query, Integer.class, name);
    }

    public List<Integer> findOrCreateByNames(List<String> names) {
        return names.stream()
            .filter(name -> name != null && !name.isBlank())
            .map(this::findOrCreateByName)
            .collect(Collectors.toList());
    }

    public boolean existsById(Integer authorId) {
        String query = "SELECT COUNT(*) FROM Authors WHERE author_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, authorId);
        return count != null && count > 0;
    }

    public List<org.example.backend.model.entity.Author> findAll() {
        String query = "SELECT author_id, name FROM Authors ORDER BY name";
        return jdbcTemplate.query(
            query,
            (rs, rowNum) -> {
                org.example.backend.model.entity.Author author = new org.example.backend.model.entity.Author();
                author.setAuthorId(rs.getInt("author_id"));
                author.setName(rs.getString("name"));
                return author;
            }
        );
    }

    public Optional<org.example.backend.model.entity.Author> findById(Integer authorId) {
        String query = "SELECT author_id, name FROM Authors WHERE author_id = ?";
        List<org.example.backend.model.entity.Author> authors = jdbcTemplate.query(
            query,
            (rs, rowNum) -> {
                org.example.backend.model.entity.Author author = new org.example.backend.model.entity.Author();
                author.setAuthorId(rs.getInt("author_id"));
                author.setName(rs.getString("name"));
                return author;
            },
            authorId
        );
        return authors.stream().findFirst();
    }

    public org.example.backend.model.entity.Author create(org.example.backend.model.entity.Author author) {
        String query = "INSERT INTO Authors (name) VALUES (?) RETURNING author_id";
        Integer authorId = jdbcTemplate.queryForObject(query, Integer.class, author.getName());
        author.setAuthorId(authorId);
        return author;
    }

    public org.example.backend.model.entity.Author update(org.example.backend.model.entity.Author author) {
        String query = "UPDATE Authors SET name = ? WHERE author_id = ?";
        int updated = jdbcTemplate.update(query, author.getName(), author.getAuthorId());
        if (updated == 0) {
            throw new RuntimeException("Author not found for update");
        }
        return author;
    }

    public void delete(Integer authorId) {
        // Check if author is referenced by any books
        String checkSql = "SELECT COUNT(*) FROM BookAuthors WHERE author_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, authorId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("Cannot delete author: it is referenced by " + count + " book(s)");
        }
        
        String query = "DELETE FROM Authors WHERE author_id = ?";
        int deleted = jdbcTemplate.update(query, authorId);
        if (deleted == 0) {
            throw new IllegalArgumentException("Author not found with ID: " + authorId);
        }
    }
}

