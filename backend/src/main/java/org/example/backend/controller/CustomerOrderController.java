package org.example.backend.controller;

import org.example.backend.model.dto.CustomerOrderDetails;
import org.example.backend.service.CustomerOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/user/orders")
public class CustomerOrderController {
    private final CustomerOrderService customerOrderService;

    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<CustomerOrderDetails>> getPastOrders(
            @PathVariable int userId) {

        List<CustomerOrderDetails> orders =
                customerOrderService.getPastOrders(userId);

        return ResponseEntity.ok(orders);
    }
}
