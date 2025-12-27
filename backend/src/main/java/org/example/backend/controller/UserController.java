package org.example.backend.controller;

import org.example.backend.model.dto.ChangePasswordRequest;
import org.example.backend.model.dto.UserUpdate;
import org.example.backend.model.enums.Role;
import org.example.backend.service.JwtService;
import org.example.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.example.backend.model.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/user")

public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService,
                          JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserUpdate userUpdate) {

        try {
            String token = extractToken(authHeader);
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            Integer userIdFromToken = jwtService.extractUserId(token);

            if (!userIdFromToken.equals(userUpdate.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only update your own profile");
            }

            userService.update(userUpdate);
            return ResponseEntity.ok("Profile updated successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected server error");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllusers() {
        try {

            List<User> profile = userService.getUsers();
            return ResponseEntity.ok(profile);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            
            Integer userId = jwtService.extractUserId(token);

            UserUpdate profile = userService.getUser(userId);

            return ResponseEntity.ok(profile);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected server error");
        }
    }
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request) {

        try {
            String token = extractToken(authHeader);
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            Integer userIdFromToken = jwtService.extractUserId(token);

            userService.updatePassword(
                    userIdFromToken,
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );

            return ResponseEntity.ok("Password updated successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected server error");
        }
    }

    @PutMapping("/make-admin/{userId}")
    public ResponseEntity<?> makeAdmin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId) {

        try {
            String token = extractToken(authHeader);
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            Role role = jwtService.extractRole(token);

            if (role != Role.Admin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only admins can promote users");
            }

            userService.makeAdmin(userId);
            return ResponseEntity.ok("User promoted to admin successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected server error");
        }
    }

    @PutMapping("/dismiss-admin/{userId}")
    public ResponseEntity<?> dismissAdmin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId) {

        try {
            String token = extractToken(authHeader);
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            Integer requesterId = jwtService.extractUserId(token);
            Role role = jwtService.extractRole(token);

            if (role != Role.Admin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only admins can dismiss admins");
            }

            if (requesterId == userId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Admin cannot dismiss himself");
            }

            userService.dismissAdmin(userId);
            return ResponseEntity.ok("Admin dismissed successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected server error");
        }
    }

    private Integer extractUserId(String authHeader) {
        String token = extractToken(authHeader);
        return jwtService.extractUserId(token);
    }

    private Role extractRole(String authHeader) {
        String token = extractToken(authHeader);
        return jwtService.extractRole(token);
    }
    @GetMapping("/role")
    public ResponseEntity<String> getRole(
            @RequestHeader("Authorization") String authHeader) {

        Role role = extractRole(authHeader);
        return ResponseEntity.ok(role.name());
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }
}
