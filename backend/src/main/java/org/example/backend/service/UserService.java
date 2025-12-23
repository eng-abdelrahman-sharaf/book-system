package org.example.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.Repository.UserRepository;
import org.example.backend.config.JwtProperties;
import org.example.backend.mapper.UserSignupMapper;
import org.example.backend.model.dto.LoginRequest;
import org.example.backend.model.dto.SignupRequest;
import org.example.backend.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSignupMapper userSignupMapper;
    private final AuthService authService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserSignupMapper userSignupMapper, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSignupMapper = userSignupMapper;
        this.authService = authService;
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
    public String login (LoginRequest loginRequest){
        String username= loginRequest.getUsername();
        String password = loginRequest.getPassword();
        User temp=userRepository.get(username);
        if(!passwordEncoder.matches(password,temp.getPassword())){
            throw new IllegalArgumentException("invalid password");
        }
        User user=new User();
        user.setRole(temp.getRole());
        user.setUserId(temp.getUserId());
        user.setFirstName(temp.getFirstName());
        JwtProperties jwtProperties = new JwtProperties();
        JwtService jwtService =new JwtService(jwtProperties);
        return jwtService.generateAccessToken(temp.getUserId(),temp.getFirstName(),temp.getRole());
    }
}
