package org.example.backend.controller;

import org.example.backend.model.dto.AddBooksRequest;
import org.example.backend.model.dto.CartBookPrice;
import org.example.backend.model.dto.RemoveBookRequest;
import org.example.backend.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService cartService;

    public ShoppingCartController(ShoppingCartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/add-books")
    public ResponseEntity<String> addBooks(@PathVariable int userId,
                                           @RequestBody AddBooksRequest request) {
        try {
            request.getItems().forEach(item ->
                    cartService.addBook(userId, item.getIsbn(), item.getQuantity()));
            return ResponseEntity.ok("Books added to cart successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add books: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartBookPrice>> viewCart(@PathVariable int userId) {
        try {
            List<CartBookPrice> items = cartService.viewCartItems(userId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/total")
    public ResponseEntity<Double> totalPrice(@PathVariable int userId) {
        try {
            double total = cartService.getCartTotal(userId);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<String> removeBook(@PathVariable int userId,
                                             @RequestBody RemoveBookRequest request) {
        try {
            cartService.removeBook(userId, request.getIsbn());
            return ResponseEntity.ok("Book removed from cart successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove book: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(@PathVariable int userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok("Cart cleared successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to clear cart: " + e.getMessage());
        }
    }
}
