package org.example.backend.Repository;

import org.example.backend.model.entity.PublisherOrder;
import org.example.backend.model.enums.OrderStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PublisherOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public PublisherOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PublisherOrder> rowMapper = (rs, rowNum) -> new PublisherOrder(
            rs.getInt("order_id"),
            rs.getString("isbn"),
            rs.getInt("quantity"),
            rs.getTimestamp("order_date").toLocalDateTime(),
            OrderStatus.valueOf(rs.getString("status"))
    );

    public PublisherOrder create(PublisherOrder order) {
        String sql = "INSERT INTO PublisherOrders (isbn, quantity, order_date, status) " +
                "VALUES (?, ?, ?, ?::order_status) RETURNING order_id";
        int orderId = jdbcTemplate.queryForObject(sql, Integer.class,
                order.getIsbn(),
                order.getQuantity(),
                java.sql.Timestamp.valueOf(order.getOrderDate()),
                order.getStatus().name());
        order.setOrderId(orderId);
        return order;
    }

    public PublisherOrder update(PublisherOrder order) {
        String sql = "UPDATE PublisherOrders SET status = ?::order_status WHERE order_id = ?";
        int updated = jdbcTemplate.update(sql,
                order.getStatus().name(),
                order.getOrderId());
        if (updated == 0) {
            throw new RuntimeException("Publisher order not found for update");
        }
        return findById(order.getOrderId()).orElseThrow(() ->
                new RuntimeException("Failed to retrieve updated publisher order"));
    }

    public Optional<PublisherOrder> findById(int orderId) {
        String sql = "SELECT * FROM PublisherOrders WHERE order_id = ?";
        List<PublisherOrder> orders = jdbcTemplate.query(sql, rowMapper, orderId);
        return orders.stream().findFirst();
    }

    public List<PublisherOrder> findAll() {
        String sql = "SELECT * FROM PublisherOrders ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<PublisherOrder> findByStatus(OrderStatus status) {
        String sql = "SELECT * FROM PublisherOrders WHERE status = ?::order_status ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, rowMapper, status.name());
    }
}

