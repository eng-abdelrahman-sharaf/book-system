package org.example.backend.controller;

import org.example.backend.model.dto.CustomerOrderDetails;
import org.example.backend.model.enums.Role;
import org.example.backend.service.CustomerOrderService;
import org.example.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/user/orders")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final JwtService jwtService;

    public CustomerOrderController(CustomerOrderService customerOrderService,
                                   JwtService jwtService) {
        this.customerOrderService = customerOrderService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrderDetails>> getPastOrders(
            @RequestHeader("Authorization") String authHeader) {

        Integer userId = extractUserId(authHeader);
        Role role = extractRole(authHeader);

        // If later you want admin to view others, this is where it goes
        // For now: customers only see their own orders

        List<CustomerOrderDetails> orders =
                customerOrderService.getPastOrders(userId);

        return ResponseEntity.ok(orders);
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
