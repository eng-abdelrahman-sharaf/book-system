package org.example.backend.controller;

import org.example.backend.model.dto.PublisherOrderResponse;
import org.example.backend.model.enums.OrderStatus;
import org.example.backend.model.enums.Role;
import org.example.backend.service.JwtService;
import org.example.backend.service.PublisherOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/publisher-orders")
public class PublisherOrderController {
    private final PublisherOrderService publisherOrderService;
    private final JwtService jwtService;

    public PublisherOrderController(PublisherOrderService publisherOrderService, JwtService jwtService) {
        this.publisherOrderService = publisherOrderService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String status) {
        try {
            List<PublisherOrderResponse> orders;
            
            if (status != null) {
                try {
                    OrderStatus orderStatus = OrderStatus.valueOf(status);
                    orders = publisherOrderService.getOrdersByStatus(orderStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid status. Must be 'Pending' or 'Confirmed'");
                }
            } else {
                orders = publisherOrderService.getAllOrders();
            }
            
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching publisher orders: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        try {
            PublisherOrderResponse order = publisherOrderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching publisher order: " + e.getMessage());
        }
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int orderId) {
        try {
            Role role = extractRole(authHeader);

            if (role != Role.Admin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only admins can confirm publisher orders");
            }

            PublisherOrderResponse confirmedOrder = publisherOrderService.confirmOrder(orderId);
            return ResponseEntity.ok(confirmedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error confirming publisher order: " + e.getMessage());
        }
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

