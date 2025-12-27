package org.example.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.Repository.RefreshTokenRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.config.JwtProperties;
import org.example.backend.mapper.UserSignupMapper;
import org.example.backend.mapper.UserUpdateMapper;
import org.example.backend.model.dto.LoginRequest;
import org.example.backend.model.dto.LoginResponse;
import org.example.backend.model.dto.SignupRequest;
import org.example.backend.model.dto.UserUpdate;
import org.example.backend.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSignupMapper userSignupMapper;
    private final AuthService authService;
    private final UserUpdateMapper userUpdateMapper;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ShoppingCartService shoppingCartService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserSignupMapper userSignupMapper, AuthService authService, UserUpdateMapper userUpdateMapper, JwtService jwtService, RefreshTokenRepository refreshTokenRepository, ShoppingCartService shoppingCartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSignupMapper = userSignupMapper;
        this.authService = authService;
        this.userUpdateMapper = userUpdateMapper;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.shoppingCartService = shoppingCartService;
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
    public LoginResponse login (LoginRequest loginRequest){
        String username= loginRequest.getUsername();
        String password = loginRequest.getPassword();
        User temp = userRepository.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(password,temp.getPassword())){
            throw new IllegalArgumentException("invalid password");
        }
        String accessToken = jwtService.generateAccessToken(temp.getUserId(), temp.getFirstName(), temp.getRole());

        // 4️⃣ Generate refresh token
        String refreshToken = jwtService.generateRefreshToken(temp.getUserId(), temp.getFirstName());

        // 5️⃣ Save refresh token in DB
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7); // e.g., 7 days
        refreshTokenRepository.save(temp.getUserId(), refreshToken, expiresAt);
        shoppingCartService.getOrCreateCart(temp.getUserId());
        // 6️⃣ Return both tokens
        return new LoginResponse(accessToken, refreshToken);
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
    public User findById(int userId){
        return userRepository.findById(userId);
    }
    public UserUpdate getUser(int userId){
        User user = findById(userId);
        return userUpdateMapper.toUserUpdate(user);
    }
    public List<User> getUsers(){
        List<User>  users = userRepository.getAllUsers();
        return users;
    }
    public User makeAdmin (int userId){
        return userRepository.makeAdmin(userId);
    }

    public User dismissAdmin (int userId){
        return userRepository.dismissAdmin(userId);
    }
}
