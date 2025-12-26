package org.example.backend.Repository;

import org.example.backend.model.entity.User;
import org.example.backend.model.enums.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public boolean existsByUsername(String username,int user_id){
        String query = " select count(*) from users where username = ? and user_id != ?";
        Integer count = jdbcTemplate.queryForObject(query,Integer.class,username,user_id);
        if(count == null||count ==0) return false;
        return true;
    }
    public boolean existsByEmail (String email,int user_id){
        String query = " select count(*) from users where email = ? and user_id != ?";
        Integer count = jdbcTemplate.queryForObject(query,Integer.class,email,user_id);
        if(count == null||count ==0) return false;
        return true;
    }
    public User create(User user){
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

    public Optional<User> getByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try {
            User user = jdbcTemplate.queryForObject(
                    query,
                    new Object[]{username},
                    (rs, rowNum) -> {
                        User tempUser = new User();
                        tempUser.setUserId(rs.getInt("user_id"));
                        tempUser.setPassword(rs.getString("password"));
                        tempUser.setFirstName(rs.getString("first_name"));

                        String role = rs.getString("role");
                        if ("Admin".equals(role))
                            tempUser.setRole(Role.Admin);
                        else if ("Customer".equals(role))
                            tempUser.setRole(Role.Customer);

                        return tempUser;
                    }
            );
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public Optional<User> findByUsername(String username) {
        String query = "SELECT user_id, username, password, first_name, last_name, email, phone, shipping_address, role, created_at " +
                "FROM Users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(query, userRowMapper(), username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public User findById(int userId){
        String sql = "select * from users where user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setShippingAddress(rs.getString("shipping_address"));
            user.setRole(Role.valueOf(rs.getString("role")));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return user;
        }, userId);
    }
    public User update(User user){
        String sql = "update users set username = ?, first_name = ?, last_name = ?, "+
                "email = ?, phone = ?, shipping_address = ? WHERE user_id = ?";
        jdbcTemplate.update(sql,user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getShippingAddress(),
                user.getUserId()
        );
        return findById(user.getUserId());
    }

    public User updatePassword(int userId,String hashedPassword){
        String sql = "update users set password = ? where user_id = ?";
        jdbcTemplate.update(sql,hashedPassword,userId);
        return findById(userId);
    }

    public User makeAdmin (int userId){
        String sql = "update users set role = 'Admin' where user_id = ?";
        jdbcTemplate.update(sql,userId);
        return findById(userId);
    }

    public User dismissAdmin(int userId){
        String sql = "update users set role = 'Customer' where user_id = ?";
        jdbcTemplate.update(sql,userId);
        return findById(userId);
    }

    public int delete (int userId){
        String sql = "delete from users where user_id = ?";
        return jdbcTemplate.update(sql,userId);
    }
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            Integer userId = rs.getInt("user_id");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String email = rs.getString("email");
            String phone = rs.getString("phone");
            String shippingAddress = rs.getString("shipping_address");
            Role role = Role.valueOf(rs.getString("role"));
            Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
            java.time.LocalDateTime createdAt = createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : null;

            return new User(userId, username, password, firstName, lastName, email, shippingAddress, phone, createdAt, role);
        };
    }
}
