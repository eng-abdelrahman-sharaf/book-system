package org.example.backend.controller;

import org.example.backend.model.dto.ChangePasswordRequest;
import org.example.backend.model.dto.UserUpdate;
import org.example.backend.model.entity.User;
import org.example.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/user")

public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> update(@RequestBody UserUpdate userUpdate){
        try{
            User user = userService.update(userUpdate);
            return ResponseEntity.ok("User profile Updated successfully: " + user.getUsername());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity <?> updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        int userId = changePasswordRequest.getUserId();
        String currentPassword = changePasswordRequest.getCurrentPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        try{
            User user = userService.updatePassword(userId,currentPassword,newPassword);
            return ResponseEntity.ok("Password Updated successfully: " + user.getUsername());
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PutMapping("/make-admin/{userId}")
    public ResponseEntity<?> makeAdmin(@PathVariable int userId){
        try {
            User user = userService.makeAdmin(userId);
            return ResponseEntity.ok("User " + user.getUsername() + " is now an admin.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/dismiss-admin/{userId}")
    public ResponseEntity<?> dismissAdmin(@PathVariable int userId){
        try {
            User user = userService.dismissAdmin(userId);
            return ResponseEntity.ok("User " + user.getUsername() + " is no longer an admin.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
