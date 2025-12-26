package org.example.backend.controller;

import org.example.backend.model.dto.ChangePasswordRequest;
import org.example.backend.model.dto.UserUpdate;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.Role;
import org.example.backend.service.JwtService;
import org.example.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        Integer userIdFromToken = extractUserId(authHeader);

        if (!userIdFromToken.equals(userUpdate.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only update your own profile");
        }

        User user = userService.update(userUpdate);
        return ResponseEntity.ok("User profile updated successfully: " + user.getUsername());
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request) {

        Integer userIdFromToken = extractUserId(authHeader);

        if (!userIdFromToken.equals(request.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only change your own password");
        }

        User user = userService.updatePassword(
                request.getUserId(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok("Password updated successfully: " + user.getUsername());
    }

    @PutMapping("/make-admin/{userId}")
    public ResponseEntity<?> makeAdmin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId) {

        Role roleFromToken = extractRole(authHeader);

        if (roleFromToken != Role.Admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only admins can promote users");
        }

        User user = userService.makeAdmin(userId);
        return ResponseEntity.ok("User " + user.getUsername() + " is now an admin.");
    }

    @PutMapping("/dismiss-admin/{userId}")
    public ResponseEntity<?> dismissAdmin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId) {

        Integer requesterId = extractUserId(authHeader);
        Role roleFromToken = extractRole(authHeader);

        if (roleFromToken != Role.Admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only admins can dismiss admins");
        }

        if (requesterId == userId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Admin cannot dismiss himself");
        }

        User user = userService.dismissAdmin(userId);
        return ResponseEntity.ok("User " + user.getUsername() + " is no longer an admin.");
    }

    private Integer extractUserId(String authHeader) {
        String token = extractToken(authHeader);
        return jwtService.extractUserId(token);
    }

    private Role extractRole(String authHeader) {
        String token = extractToken(authHeader);
        return jwtService.extractRole(token);
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }
}
