package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.model.dto.LoginRequest;
import org.example.backend.model.dto.LoginResponse;

import org.example.backend.model.dto.LogoutRequest;
import org.example.backend.model.dto.SignupRequest;
import org.example.backend.model.entity.User;
import org.example.backend.service.AuthService;
import org.example.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for user login, signup, and token management")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;


    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }
    @Operation(summary = "User Signup", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request){
        try{
            User user = userService.signup(request);
            return ResponseEntity.ok("User registered successfully: " + user.getUsername());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Log the full exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage() + " (Check server logs for details)");
        }
    }



    @Operation(summary = "Refresh Access Token", description = "Generate a new access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }



    // Inner class for refresh token request
    public static class RefreshTokenRequest {
        private String refreshToken;

        public RefreshTokenRequest() {
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
    @Operation(summary = "User Login", description = "Authenticate user and receive access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        try {
            LoginResponse token = userService.login(request);

            return ResponseEntity.ok(token);  // return the DTO directly
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // or handle with @ControllerAdvice
        }
    }


    @Operation(summary = "User Logout", description = "Invalidate refresh token and log out user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody LogoutRequest request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok("Logged out successfully");
    }

}
