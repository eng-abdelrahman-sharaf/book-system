package org.example.backend.controller;

import org.example.backend.model.dto.CheckoutRequest;
import org.example.backend.model.entity.CustomerOrder;
import org.example.backend.service.CheckoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/cart")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable int userId,
                                      @RequestBody CheckoutRequest request) {
        try {
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
}
