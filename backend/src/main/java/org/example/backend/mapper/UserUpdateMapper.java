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
    public UserUpdate toUserUpdate(User user){
        UserUpdate userU = new UserUpdate();
        userU.setFirstName(user.getFirstName());
        userU.setLastName(user.getLastName());
        userU.setEmail(user.getEmail());
        userU.setPhone(user.getPhone());
        userU.setShippingAddress(user.getShippingAddress());
        userU.setUserId(user.getUserId());
        userU.setUsername(user.getUsername());
        return userU;
    }
}
