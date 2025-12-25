package org.example.backend.Repository;

import org.example.backend.model.dto.CustomerOrderDetails;
import org.example.backend.model.entity.CustomerOrder;
import org.example.backend.model.entity.CustomerOrderItem;
import org.example.backend.model.enums.OrderStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class CustomerOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CustomerOrder> rowMapper = (rs, rowNum) -> new CustomerOrder(
            rs.getInt("order_id"),
            rs.getInt("user_id"),
            rs.getTimestamp("order_date").toLocalDateTime(),
            rs.getDouble("total_amount"),
            OrderStatus.valueOf(rs.getString("status"))
    );

    public CustomerOrder create(CustomerOrder order) {
        String sql = "INSERT INTO CustomerOrders (user_id, order_date, total_amount, status) " +
                "VALUES (?, ?, ?, ?::order_status) RETURNING order_id";
        int orderId = jdbcTemplate.queryForObject(sql, Integer.class,
                order.getUserId(),
                java.sql.Timestamp.valueOf(order.getOrderDate()),
                order.getTotalAmount(),
                order.getStatus().name());
        order.setOrderId(orderId);
        return order;
    }

    public CustomerOrder update(CustomerOrder order) {
        String sql = "UPDATE CustomerOrders SET total_amount = ?, status = ? WHERE order_id = ?";
        int updated = jdbcTemplate.update(sql,
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getOrderId());
        if (updated == 0) throw new RuntimeException("Order not found for update");
        return findById(order.getOrderId()).orElseThrow(() ->
                new RuntimeException("Failed to retrieve updated order"));
    }

    public CustomerOrder delete(int orderId) {
        CustomerOrder existing = findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for deletion"));
        String sql = "DELETE FROM CustomerOrders WHERE order_id = ?";
        jdbcTemplate.update(sql, orderId);
        return existing;
    }

    public Optional<CustomerOrder> findById(int orderId) {
        String sql = "SELECT * FROM CustomerOrders WHERE order_id = ?";
        return jdbcTemplate.query(sql, rowMapper, orderId).stream().findFirst();
    }

    public List<CustomerOrder> findAllByUser(int userId) {
        String sql = "SELECT * FROM CustomerOrders WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<CustomerOrderDetails> findAllOrdersWithItemsByUser(int userId) {
        String sql = """
            SELECT 
                o.order_id, o.order_date, o.total_amount,
                i.isbn, i.quantity, i.price,b.title
            FROM CustomerOrders o
            LEFT JOIN CustomerOrderItems i ON o.order_id = i.order_id
            LEFT JOIN Books b on b.isbn = i.isbn 
            WHERE o.user_id = ?
            ORDER BY o.order_date DESC, o.order_id, i.isbn
        """;

        return jdbcTemplate.query(sql, (ResultSet rs) -> {
            Map<Integer, CustomerOrderDetails> map = new LinkedHashMap<>();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                CustomerOrderDetails order = map.get(orderId);
                if (order == null) {
                    order = new CustomerOrderDetails();
                    order.setOrderId(orderId);
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setTitle(rs.getString("title"));
                    order.setItems(new ArrayList<>());
                    map.put(orderId, order);
                }

                // Add item if exists
                String isbn = rs.getString("isbn");
                if (isbn != null) {
                    CustomerOrderItem item = new CustomerOrderItem();
                    item.setOrderId(orderId);
                    item.setIsbn(isbn);
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));

                    order.getItems().add(item);
                }
            }

            return new ArrayList<>(map.values());
        }, userId);
    }
}
