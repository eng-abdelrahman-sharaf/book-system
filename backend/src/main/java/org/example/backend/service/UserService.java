package org.example.backend.service;

import org.example.backend.Repository.UserRepository;
import org.example.backend.mapper.UserSignupMapper;
import org.example.backend.mapper.UserUpdateMapper;
import org.example.backend.model.dto.SignupRequest;
import org.example.backend.model.dto.UserUpdate;
import org.example.backend.model.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSignupMapper userSignupMapper;
    private final UserUpdateMapper userUpdateMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserSignupMapper userSignupMapper,UserUpdateMapper userUpdateMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSignupMapper = userSignupMapper;
        this.userUpdateMapper = userUpdateMapper;
    }

    @Transactional
    public User signup (SignupRequest signupRequest){
        String username= signupRequest.getUsername();
        String email = signupRequest.getEmail();
        if(userRepository.existsByUsername(username,-1)){
            throw new IllegalArgumentException("username already exists");
        }
        if(userRepository.existsByEmail(email,-1)) {
            throw new IllegalArgumentException("email already exists");
        }
        User user =userSignupMapper.toUser(signupRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.create(user);
    }
    @Transactional
    public User update(UserUpdate userUpdate){
        int userId = userUpdate.getUserId();
        String username= userUpdate.getUsername();
        String email = userUpdate.getEmail();
        if(userRepository.existsByUsername(username,userId)){
            throw new IllegalArgumentException("username already exists");
        }
        if(userRepository.existsByEmail(email,userId)) {
            throw new IllegalArgumentException("email already exists");
        }
        User user =userUpdateMapper.toUser(userUpdate);
        return userRepository.update(user);
    }

    public User updatePassword(int userId,String currentPassword,String newPassword){
        User user = userRepository.findById(userId);
        if(!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw new RuntimeException("Incorrect current password");
        }
        String hashed = passwordEncoder.encode(newPassword);
        return userRepository.updatePassword(userId,hashed);
    }

    public User makeAdmin (int userId){
        return userRepository.makeAdmin(userId);
    }

    public User dismissAdmin (int userId){
        return userRepository.dismissAdmin(userId);
    }


}
