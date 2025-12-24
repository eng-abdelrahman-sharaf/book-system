package org.example.backend.Repository;

import org.example.backend.model.dto.CartBookPrice;
import org.example.backend.model.entity.Book;
import org.example.backend.model.entity.ShoppingCart;
import org.example.backend.model.entity.ShoppingCartItem;
import org.example.backend.model.enums.CategoryType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShoppingCartRepository {
    private final JdbcTemplate jdbcTemplate;
    public ShoppingCartRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    private final RowMapper<ShoppingCart> cartMapper = (rs, rowNum) -> {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartId(rs.getInt("cart_id"));
        cart.setUserId(rs.getInt("user_id"));
        cart.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return cart;
    };

    private final RowMapper<ShoppingCartItem> itemMapper = (rs, rowNum) -> {
        ShoppingCartItem item = new ShoppingCartItem();
        item.setCartId(rs.getInt("cart_id"));
        item.setIsbn(rs.getString("isbn"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    };

    private final RowMapper<Book> bookMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setPublisherId(rs.getInt("publisher_id"));
        book.setPublicationYear(rs.getObject("publication_year", Integer.class));
        book.setSellingPrice(rs.getDouble("selling_price"));
        book.setCategory(CategoryType.valueOf(rs.getString("category")));
        book.setNumberOfBooks(rs.getInt("number_of_books"));
        book.setThreshold(rs.getInt("threshold"));
        return book;
    };
    public ShoppingCart getCartByUser(int userId) {
        String sql = "SELECT * FROM ShoppingCart WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";
        List<ShoppingCart> carts = jdbcTemplate.query(sql, cartMapper, userId);
        return carts.isEmpty() ? null : carts.get(0);
    }

    public ShoppingCart createCart(int userId) {
        String sql = "INSERT INTO ShoppingCart (user_id) VALUES (?)";
        jdbcTemplate.update(sql, userId);
        return getCartByUser(userId);
    }

    public void addBook(int cartId,String isbn,int quantity){
        String checkSql = "SELECT COUNT(*) FROM ShoppingCartItems WHERE cart_id = ? AND isbn = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, cartId, isbn);

        if (count != null && count > 0) {
            String updateSql = "UPDATE ShoppingCartItems SET quantity = quantity + ? WHERE cart_id = ? AND isbn = ?";
            jdbcTemplate.update(updateSql, quantity, cartId, isbn);
        } else {
            String insertSql = "INSERT INTO ShoppingCartItems (cart_id, isbn, quantity) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, cartId, isbn, quantity);
        }
    }

    public void removeBook(int cartId, String isbn) {
        String sql = "DELETE FROM ShoppingCartItems WHERE cart_id = ? AND isbn = ?";
        jdbcTemplate.update(sql, cartId, isbn);
    }

    public List<ShoppingCartItem> getCartItems(int cartId) {
        String sql = "SELECT * FROM ShoppingCartItems WHERE cart_id = ?";
        return jdbcTemplate.query(sql, itemMapper, cartId);
    }

    public List<Book> getCartBooks(int cartId) {
        String sql = """
            SELECT b.*
            FROM ShoppingCartItems sci
            JOIN Books b ON sci.isbn = b.isbn
            WHERE sci.cart_id = ?
            """;
        return jdbcTemplate.query(sql, bookMapper, cartId);
    }

    public double getCartTotalPrice(int cartId) {
        String sql = """
            SELECT SUM(b.selling_price * sci.quantity) AS total
            FROM ShoppingCartItems sci
            JOIN Books b ON sci.isbn = b.isbn
            WHERE sci.cart_id = ?
            """;
        double total = jdbcTemplate.queryForObject(sql, Double.class, cartId);

        return total;
    }

    public List<CartBookPrice> getIndividualPrices(int cartId) {
        String sql = """
            SELECT b.isbn, b.title, b.selling_price, sci.quantity, (b.selling_price * sci.quantity) AS total_price
            FROM ShoppingCartItems sci
            JOIN Books b ON sci.isbn = b.isbn
            WHERE sci.cart_id = ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CartBookPrice p = new CartBookPrice();
            p.setIsbn(rs.getString("isbn"));
            p.setTitle(rs.getString("title"));
            p.setUnitPrice(rs.getDouble("selling_price"));
            p.setQuantity(rs.getInt("quantity"));
            p.setTotalPrice(rs.getDouble("total_price"));
            return p;
        }, cartId);
    }

}
