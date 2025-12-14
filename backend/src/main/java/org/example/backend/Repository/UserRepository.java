package org.example.backend.Repository;

import org.example.backend.model.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public boolean existsByUsername(String username){
        String query = " select count(*) from users where username = ?";
        Integer count = jdbcTemplate.queryForObject(query,Integer.class,username);
        if(count == null||count ==0) return false;
        return true;
    }
    public boolean existsByEmail (String email){
        String query = " select count(*) from users where email = ?";
        Integer count = jdbcTemplate.queryForObject(query,Integer.class,email);
        if(count == null||count ==0) return false;
        return true;
    }
    public User save(User user){
        String query = "insert into users (username, password, first_name, last_name, email, phone, shipping_address, role)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?::role_enum) returning user_id, created_at";
        User returned = jdbcTemplate.queryForObject(query, new Object[]{
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getShippingAddress(),
                user.getRole().name()
        }, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                user.setUserId(rs.getInt("user_id"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return user;
            }
        });
        return returned;
    }
    // update, delete but when we work on the crud
    public int update(User user){
        String sql = "update users set username = ?, password = ?, first_name = ?, last_name = ?, "+
                "role = ?,email = ?, phone = ?, shipping_address = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql,user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getEmail(),
                user.getPhone(),
                user.getShippingAddress(),
                user.getUserId()
        );
    }
}
