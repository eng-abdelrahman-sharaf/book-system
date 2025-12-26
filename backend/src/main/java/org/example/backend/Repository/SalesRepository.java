package org.example.backend.Repository;

import org.example.backend.model.dto.BookOrderHistory;
import org.example.backend.model.dto.BookSalesHistory;
import org.example.backend.model.dto.TotalSales;
import org.example.backend.model.dto.customerHistory;
import org.example.backend.model.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.example.backend.model.entity.Book;


@Repository
public class SalesRepository {
    private final JdbcTemplate jdbcTemplate;
    public SalesRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public TotalSales getsales(){
        String sql =
                "SELECT " +
                        "   SUM(coi.quantity * coi.price) AS total_sales, " +
                        "   COUNT(DISTINCT co.order_id) AS total_orders, " +
                        "   TO_CHAR(date_trunc('month', CURRENT_DATE) - INTERVAL '1 month', 'Month') AS month " +
                        "FROM CustomerOrders co " +
                        "JOIN CustomerOrderItems coi ON co.order_id = coi.order_id " +
                        "WHERE co.order_date >= date_trunc('month', CURRENT_DATE) - INTERVAL '1 month' " +
                        "AND co.order_date < date_trunc('month', CURRENT_DATE)";


        TotalSales totalSales = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new TotalSales(
                        rs.getInt("total_sales"),   // sum of sales
                        rs.getInt("total_orders"),  // count of orders
                        rs.getString("month").trim() // month name (trim to remove spaces)
                )
        );
        return totalSales ;

    }
    public List<BookSalesHistory> getBookSalesHistoryByDate(String isbn,String date) {
        String sql = "SELECT " +
                "  SUM(COI.quantity) AS number_of_sold_books, " +
                "  SUM(COI.price) AS total_profit, " +
                "  B.isbn, B.title " +
                "FROM CustomerOrders CO " +
                "JOIN CustomerOrderItems COI ON CO.order_id = COI.order_id " +
                "JOIN Books B ON B.isbn = COI.isbn " +
                "WHERE CO.order_date::date = ? " + // filter by input date
                "GROUP BY B.isbn, B.title";

        List<BookSalesHistory> results = jdbcTemplate.query(sql, new Object[]{java.sql.Date.valueOf(date)},
                (rs, rowNum) -> {
                    BookSalesHistory tempSalesHistory = new BookSalesHistory();
                    tempSalesHistory.setQuantity(rs.getInt("number_of_sold_books"));
                    tempSalesHistory.setTotalprofit(rs.getInt("total_profit"));
                    Book book = new Book();
                    book.setIsbn(rs.getString("isbn"));
                    book.setTitle(rs.getString("title"));
                    tempSalesHistory.setBook(book);
                    return tempSalesHistory;
                });

        return results; // returns all books sold on that date
    }

    public List<BookSalesHistory> getTopbooksales() {
        String sql = "SELECT " +
                "  SUM(COI.quantity) AS number_of_sold_books, " +
                "  SUM(COI.price) AS total_profit, " +
                "  B.isbn, B.title " +
                "FROM CustomerOrders CO " +
                "JOIN CustomerOrderItems COI ON CO.order_id = COI.order_id " +
                "JOIN Books B ON B.isbn = COI.isbn " +
                "WHERE CO.order_date >= current_date - interval '3 months' " +
                "GROUP BY B.isbn, B.title " +
                "ORDER BY number_of_sold_books DESC " +
                "LIMIT 10";

        List<BookSalesHistory> results = jdbcTemplate.query(sql, new RowMapper<BookSalesHistory>() {
            @Override
            public BookSalesHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
                BookSalesHistory tempSalesHistory = new BookSalesHistory();
                tempSalesHistory.setQuantity(rs.getInt("number_of_sold_books"));
                tempSalesHistory.setTotalprofit(rs.getInt("total_profit"));
                Book book = new Book();
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                tempSalesHistory.setBook(book);
                return tempSalesHistory;
            }
        });
        return results;

    }
    public BookOrderHistory getBookOrderHistory(String bookId) {
        String sql = "SELECT B.isbn, B.title, COUNT(PO.order_id) AS numberoforders " +
                "FROM PublisherOrders PO " +
                "JOIN Books B ON B.isbn = PO.isbn " +
                "WHERE B.isbn = ? " +
                "GROUP BY B.isbn, B.title";

        List<BookOrderHistory> results = jdbcTemplate.query(
                sql,
                new Object[]{bookId},
                (rs, rowNum) -> {
                    BookOrderHistory history = new BookOrderHistory();

                    Book book = new Book();
                    book.setIsbn(rs.getString("isbn"));
                    book.setTitle(rs.getString("title"));
                    history.setBook(book);

                    history.setNumberoforders(rs.getInt("numberoforders"));
                    return history;
                }
        );

        if (results.isEmpty()) {
            BookOrderHistory emptyHistory = new BookOrderHistory();
            Book book = new Book();
            book.setIsbn(bookId);
            emptyHistory.setBook(book);
            emptyHistory.setNumberoforders(0);
            return emptyHistory;
        } else {
            return results.get(0);
        }
    }
    public List<customerHistory> getTopCustomers() {
        String sql = "SELECT u.user_id, u.first_name, u.last_name, SUM(co.total_amount) AS total_amount " +
                "FROM CustomerOrders co " +
                "JOIN Users u ON co.user_id = u.user_id " +
                "GROUP BY u.user_id, u.first_name, u.last_name " +
                "ORDER BY total_amount DESC " +
                "LIMIT 5";

        List<customerHistory> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));

            int numberOfOrders = rs.getInt("total_amount");
            return new customerHistory(user, numberOfOrders);
        });

        return results;
    }



}
