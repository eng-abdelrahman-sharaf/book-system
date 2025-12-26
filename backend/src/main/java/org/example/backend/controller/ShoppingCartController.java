package org.example.backend.controller;

import org.example.backend.model.dto.AddBooksRequest;
import org.example.backend.model.dto.CartBookPrice;
import org.example.backend.model.dto.RemoveBookRequest;
import org.example.backend.service.JwtService;
import org.example.backend.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService cartService;
    private final JwtService jwtService;

    public ShoppingCartController(ShoppingCartService cartService,JwtService jwtService) {
        this.cartService = cartService;
        this.jwtService=jwtService;
    }
    private Integer extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7); // remove "Bearer "
        return jwtService.extractUserId(token);
    }
    @PostMapping("/add-books")
    public ResponseEntity<String> addBooks(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody AddBooksRequest request) {
        Integer userId = extractUserIdFromHeader(authHeader);
        try {
            request.getItems().forEach(item ->
                    cartService.addBook(userId, item.getIsbn(), item.getQuantity()));
            return ResponseEntity.ok("Books added to cart successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add books: " + e.getMessage());
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartBookPrice>> viewCart( @RequestHeader("Authorization") String authHeader) {
        Integer userId = extractUserIdFromHeader(authHeader);
        try {
            List<CartBookPrice> items = cartService.viewCartItems(userId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total")
    public ResponseEntity<?> totalPrice(@RequestHeader("Authorization") String authHeader) {
        Integer userId = extractUserIdFromHeader(authHeader);
        try {
            System.out.println("hello");
            double total = cartService.getCartTotal(userId);
            System.out.println("messi");
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeBook(@RequestHeader("Authorization") String authHeader,
                                             @RequestBody RemoveBookRequest request) {
        Integer userId = extractUserIdFromHeader(authHeader);
        try {
            cartService.removeBook(userId, request.getIsbn());
            return ResponseEntity.ok("Book removed from cart successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove book: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("Authorization") String authHeader) {
        Integer userId = extractUserIdFromHeader(authHeader);
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok("Cart cleared successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to clear cart: " + e.getMessage());
        }
    }
}
