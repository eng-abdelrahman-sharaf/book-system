package org.example.backend.service;

import org.example.backend.Repository.CustomerOrderItemRepository;
import org.example.backend.Repository.CustomerOrderRepository;
import org.example.backend.model.dto.CustomerOrderDetails;
import org.example.backend.model.entity.CustomerOrder;
import org.example.backend.model.entity.CustomerOrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerOrderService {
    private final CustomerOrderRepository customerOrderRepository;

    public CustomerOrderService(CustomerOrderItemRepository customerOrderItemRepository, CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    public List<CustomerOrderDetails> getPastOrders(int userId) {
        return customerOrderRepository.findAllOrdersWithItemsByUser(userId);
    }

}
