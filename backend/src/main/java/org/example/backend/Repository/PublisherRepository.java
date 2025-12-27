package org.example.backend.Repository;

import org.example.backend.model.entity.Publisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PublisherRepository {
    private final JdbcTemplate jdbcTemplate;

    public PublisherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Publisher> rowMapper = (rs, rowNum) -> {
        Publisher publisher = new Publisher();
        publisher.setPublisherId(rs.getInt("publisher_id"));
        publisher.setName(rs.getString("name"));
        publisher.setAddress(rs.getString("address"));
        publisher.setPhone(rs.getString("phone"));
        return publisher;
    };

    public Publisher create(Publisher publisher) {
        String sql = "INSERT INTO Publishers (name, address, phone) VALUES (?, ?, ?) RETURNING publisher_id";
        Integer publisherId = jdbcTemplate.queryForObject(sql, Integer.class,
                publisher.getName(),
                publisher.getAddress(),
                publisher.getPhone());
        publisher.setPublisherId(publisherId);
        return publisher;
    }

    public Publisher update(Publisher publisher) {
        String sql = "UPDATE Publishers SET name = ?, address = ?, phone = ? WHERE publisher_id = ?";
        int updated = jdbcTemplate.update(sql,
                publisher.getName(),
                publisher.getAddress(),
                publisher.getPhone(),
                publisher.getPublisherId());
        if (updated == 0) {
            throw new RuntimeException("Publisher not found for update");
        }
        return publisher;
    }

    public void delete(Integer publisherId) {
        String checkSql = "SELECT COUNT(*) FROM Books WHERE publisher_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, publisherId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("Cannot delete publisher: it is referenced by " + count + " book(s)");
        }
        
        String sql = "DELETE FROM Publishers WHERE publisher_id = ?";
        int deleted = jdbcTemplate.update(sql, publisherId);
        if (deleted == 0) {
            throw new IllegalArgumentException("Publisher not found with ID: " + publisherId);
        }
    }

    public List<Publisher> findAll() {
        String sql = "SELECT * FROM Publishers ORDER BY name";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Publisher> findById(Integer publisherId) {
        String sql = "SELECT * FROM Publishers WHERE publisher_id = ?";
        List<Publisher> publishers = jdbcTemplate.query(sql, rowMapper, publisherId);
        return publishers.stream().findFirst();
    }

    public boolean existsById(Integer publisherId) {
        String sql = "SELECT COUNT(*) FROM Publishers WHERE publisher_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, publisherId);
        return count != null && count > 0;
    }
}

