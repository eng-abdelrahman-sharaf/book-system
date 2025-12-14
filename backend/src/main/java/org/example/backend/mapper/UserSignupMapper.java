package org.example.backend.mapper;

import org.example.backend.model.dto.SignupRequest;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.Role;
import org.springframework.stereotype.Component;


@Component

public class UserSignupMapper {
    public User toUser (SignupRequest dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(Role.Customer);
        user.setShippingAddress(dto.getShippingAddress());
        user.setPhone(dto.getPhone());
        return user;
    }
}
