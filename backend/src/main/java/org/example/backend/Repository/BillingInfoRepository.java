package org.example.backend.Repository;

import org.example.backend.model.entity.BillingInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BillingInfoRepository {
    private final JdbcTemplate jdbcTemplate;

    public BillingInfoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BillingInfo> rowMapper = (rs, rowNum) -> new BillingInfo(
            rs.getInt("user_id"),
            rs.getString("card_number"),
            rs.getDate("expiration_date").toLocalDate(),
            rs.getString("billing_address")
    );
    public Optional<BillingInfo> findByUserId(int userId) {
        String sql = "SELECT * FROM BillingInfo WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId).stream().findFirst();
    }
    public BillingInfo create(BillingInfo billingInfo) {
        String sql = "INSERT INTO BillingInfo (user_id, card_number, expiration_date, billing_address) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                billingInfo.getUserId(),
                billingInfo.getCardNumber(),
                java.sql.Date.valueOf(billingInfo.getExpirationDate()),
                billingInfo.getBillingAddress()
        );
        return findByUserId(billingInfo.getUserId()).orElseThrow(() ->
                new RuntimeException("Failed to create BillingInfo"));
    }

    public BillingInfo update(BillingInfo billingInfo) {
        String sql = "UPDATE BillingInfo SET card_number = ?, expiration_date = ?, billing_address = ? " +
                "WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql,
                billingInfo.getCardNumber(),
                java.sql.Date.valueOf(billingInfo.getExpirationDate()),
                billingInfo.getBillingAddress(),
                billingInfo.getUserId()
        );
        if (updated == 0) throw new RuntimeException("BillingInfo not found for update");
        return findByUserId(billingInfo.getUserId()).orElseThrow(() ->
                new RuntimeException("Failed to retrieve updated BillingInfo"));
    }

    void  delete(int userId) {
        String sql = "DELETE FROM BillingInfo WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public java.util.List<BillingInfo> findAll() {
        String sql = "SELECT * FROM BillingInfo";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
