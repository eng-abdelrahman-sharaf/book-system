package org.example.backend.mapper;

import org.example.backend.model.dto.UserUpdate;
import org.example.backend.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateMapper {

    public User toUser(UserUpdate dto){
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setShippingAddress(dto.getShippingAddress());
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        return user;
    }
}
