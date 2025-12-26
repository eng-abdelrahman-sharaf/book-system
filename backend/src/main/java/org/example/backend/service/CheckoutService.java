package org.example.backend.service;

import org.example.backend.Repository.*;
import org.example.backend.model.dto.CartBookPrice;
import org.example.backend.model.dto.CheckoutRequest;
import org.example.backend.model.entity.*;
import org.example.backend.model.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {
    private final ShoppingCartRepository cartRepository;
    private final CustomerOrderRepository orderRepository;
    private final CustomerOrderItemRepository orderItemRepository;
    private final BillingInfoRepository billingInfoRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartService shoppingCartService;

    public CheckoutService(ShoppingCartRepository cartRepository, CustomerOrderRepository orderRepository, CustomerOrderItemRepository orderItemRepository, BillingInfoRepository billingInfoRepository, BookRepository bookRepository, ShoppingCartService shoppingCartService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.billingInfoRepository = billingInfoRepository;
        this.bookRepository = bookRepository;
        this.shoppingCartService = shoppingCartService;
    }
    private void validateCreditCard(String cardNumber, LocalDate expiryDate) {
        if (cardNumber.length() != 16) {
            throw new RuntimeException("Invalid card number length");
        }
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Credit card expired");
        }
    }

    @Transactional
    public CustomerOrder checkout (int userId, CheckoutRequest request){
        ShoppingCart shoppingCart = shoppingCartService.getOrCreateCart(userId);
        List<CartBookPrice> cartItems = cartRepository.getIndividualPrices(shoppingCart.getCartId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        BillingInfo billingInfo = billingInfoRepository.findByUserId(userId).orElse(null);
        if (billingInfo == null) {
            if (request.getCardNumber() == null || request.getExpirationDate() == null || request.getBillingAddress() == null) {
                throw new RuntimeException("No stored card. Must provide card info");
            }
            billingInfo = new BillingInfo(userId, request.getCardNumber(), request.getExpirationDate(), request.getBillingAddress());
            billingInfo = billingInfoRepository.create(billingInfo);
        } else {
            if (request.getCardNumber() != null && request.getExpirationDate() != null && request.getBillingAddress() != null) {
                billingInfo.setCardNumber(request.getCardNumber());
                billingInfo.setExpirationDate(request.getExpirationDate());
                billingInfo.setBillingAddress(request.getBillingAddress());
                billingInfo = billingInfoRepository.update(billingInfo);
            }
        }
        validateCreditCard(billingInfo.getCardNumber(), billingInfo.getExpirationDate());
        for (CartBookPrice item : cartItems) {
            Book book = bookRepository.findByIsbn(item.getIsbn());
            if (book.getNumberOfBooks() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
            }
        }
        double totalAmount = cartRepository.getCartTotalPrice(shoppingCart.getCartId());
        CustomerOrder order = new CustomerOrder();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.Confirmed);
        order = orderRepository.create(order);
        for (CartBookPrice item : cartItems) {
            orderItemRepository.create(new CustomerOrderItem(
                    order.getOrderId(),
                    item.getIsbn(),
                    item.getQuantity(),
                    item.getUnitPrice()
            ));
            bookRepository.deductStock(item.getIsbn(), item.getQuantity());
        }
        shoppingCartService.clearCart(userId);
        return order;
    }
    public String getSavedCardNumber(int userId) {
        return billingInfoRepository.findByUserId(userId)
                .map(b -> "**** **** **** " + b.getCardNumber().substring(12))
                .orElse(null);
    }
}
