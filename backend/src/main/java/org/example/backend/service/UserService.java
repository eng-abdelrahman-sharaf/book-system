package org.example.backend.service;

import org.example.backend.Repository.UserRepository;
import org.example.backend.mapper.UserSignupMapper;
import org.example.backend.model.dto.SignupRequest;
import org.example.backend.model.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSignupMapper userSignupMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserSignupMapper userSignupMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSignupMapper = userSignupMapper;
    }

    @Transactional
    public User signup (SignupRequest signupRequest){
        String username= signupRequest.getUsername();
        String email = signupRequest.getEmail();
        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("username already exists");
        }
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email already exists");
        }
        User user =userSignupMapper.toUser(signupRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
