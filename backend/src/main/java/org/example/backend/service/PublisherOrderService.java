package org.example.backend.service;

import org.example.backend.Repository.BookRepository;
import org.example.backend.Repository.PublisherOrderRepository;
import org.example.backend.model.dto.PublisherOrderResponse;
import org.example.backend.model.entity.Book;
import org.example.backend.model.entity.PublisherOrder;
import org.example.backend.model.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherOrderService {
    private final PublisherOrderRepository publisherOrderRepository;
    private final BookRepository bookRepository;

    public PublisherOrderService(PublisherOrderRepository publisherOrderRepository, BookRepository bookRepository) {
        this.publisherOrderRepository = publisherOrderRepository;
        this.bookRepository = bookRepository;
    }

    public List<PublisherOrderResponse> getAllOrders() {
        List<PublisherOrder> orders = publisherOrderRepository.findAll();
        return mapToResponse(orders);
    }

    public List<PublisherOrderResponse> getOrdersByStatus(OrderStatus status) {
        List<PublisherOrder> orders = publisherOrderRepository.findByStatus(status);
        return mapToResponse(orders);
    }

    public PublisherOrderResponse getOrderById(int orderId) {
        PublisherOrder order = publisherOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Publisher order not found with ID: " + orderId));
        return mapToResponse(order);
    }

    @Transactional
    public PublisherOrderResponse confirmOrder(int orderId) {
        PublisherOrder order = publisherOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Publisher order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.Confirmed) {
            throw new IllegalArgumentException("Order is already confirmed");
        }

        order.setStatus(OrderStatus.Confirmed);
        PublisherOrder updatedOrder = publisherOrderRepository.update(order);
        
        return mapToResponse(updatedOrder);
    }

    private List<PublisherOrderResponse> mapToResponse(List<PublisherOrder> orders) {
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PublisherOrderResponse mapToResponse(PublisherOrder order) {
        Book book = bookRepository.read(order.getIsbn());
        String bookTitle = book != null ? book.getTitle() : "Unknown";
        
        return new PublisherOrderResponse(
                order.getOrderId(),
                order.getIsbn(),
                bookTitle,
                order.getQuantity(),
                order.getOrderDate(),
                order.getStatus()
        );
    }
}

