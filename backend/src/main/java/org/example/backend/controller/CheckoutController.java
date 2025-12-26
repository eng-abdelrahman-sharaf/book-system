package org.example.backend.controller;

import org.example.backend.model.dto.CheckoutRequest;
import org.example.backend.model.entity.CustomerOrder;
import org.example.backend.service.CheckoutService;
import org.example.backend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/cart")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final JwtService jwtService;

    public CheckoutController(CheckoutService checkoutService,
                              JwtService jwtService) {
        this.checkoutService = checkoutService;
        this.jwtService = jwtService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CheckoutRequest request) {

        try {
            Integer userId = extractUserId(authHeader);

            CustomerOrder order = checkoutService.checkout(userId, request);
            return ResponseEntity.ok(order);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    private Integer extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtService.extractUserId(token);
    }
}