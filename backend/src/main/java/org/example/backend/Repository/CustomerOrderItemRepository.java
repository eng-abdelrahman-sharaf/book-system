package org.example.backend.Repository;

import org.example.backend.model.entity.CustomerOrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerOrderItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomerOrderItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CustomerOrderItem> rowMapper = (rs, rowNum) -> new CustomerOrderItem(
            rs.getInt("order_id"),
            rs.getString("isbn"),
            rs.getInt("quantity"),
            rs.getDouble("price")
    );

    public CustomerOrderItem create(CustomerOrderItem item) {
        String sql = "INSERT INTO CustomerOrderItems (order_id, isbn, quantity, price) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                item.getOrderId(),
                item.getIsbn(),
                item.getQuantity(),
                item.getPrice());
        return item;
    }

    public CustomerOrderItem update(CustomerOrderItem item) {
        String sql = "UPDATE CustomerOrderItems SET quantity = ?, price = ? WHERE order_id = ? AND isbn = ?";
        int updated = jdbcTemplate.update(sql,
                item.getQuantity(),
                item.getPrice(),
                item.getOrderId(),
                item.getIsbn());
        if (updated == 0) throw new RuntimeException("Order item not found for update");
        return findById(item.getOrderId(), item.getIsbn())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve updated order item"));
    }

    public CustomerOrderItem delete(int orderId, String isbn) {
        CustomerOrderItem existing = findById(orderId, isbn)
                .orElseThrow(() -> new RuntimeException("Order item not found for deletion"));
        String sql = "DELETE FROM CustomerOrderItems WHERE order_id = ? AND isbn = ?";
        jdbcTemplate.update(sql, orderId, isbn);
        return existing;
    }

    public Optional<CustomerOrderItem> findById(int orderId, String isbn) {
        String sql = "SELECT * FROM CustomerOrderItems WHERE order_id = ? AND isbn = ?";
        return jdbcTemplate.query(sql, rowMapper, orderId, isbn).stream().findFirst();
    }

    public List<CustomerOrderItem> findAllByOrder(int orderId) {
        String sql = "SELECT * FROM CustomerOrderItems WHERE order_id = ?";
        return jdbcTemplate.query(sql, rowMapper, orderId);
    }
}
