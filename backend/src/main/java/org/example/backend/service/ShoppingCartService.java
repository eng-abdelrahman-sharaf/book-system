package org.example.backend.service;

import org.example.backend.Repository.ShoppingCartRepository;
import org.example.backend.model.dto.CartBookPrice;
import org.example.backend.model.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {
    public final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository){
        this.shoppingCartRepository=shoppingCartRepository;
    }

    public ShoppingCart getOrCreateCart(int userId) {
        ShoppingCart cart = shoppingCartRepository.getCartByUser(userId);
        if (cart == null) {
            shoppingCartRepository.createCart(userId);
            cart = shoppingCartRepository.getCartByUser(userId);
        }
        return cart;
    }
    public void addBook(int userId, String isbn, int quantity) {
        ShoppingCart cart = getOrCreateCart(userId);
        shoppingCartRepository.addBook(cart.getCartId(), isbn, quantity);
    }

    public void removeBook(int userId, String isbn) {
        ShoppingCart cart = getOrCreateCart(userId);
        shoppingCartRepository.removeBook(cart.getCartId(), isbn);
    }

    public List<CartBookPrice> viewCartItems(int userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        return shoppingCartRepository.getIndividualPrices(cart.getCartId());
    }

    public double getCartTotal(int userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        System.out.println("tot");
        double tot =shoppingCartRepository.getCartTotalPrice(cart.getCartId());
        System.out.println(tot);
        return tot;
    }

    public void clearCart(int userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        shoppingCartRepository.getCartItems(cart.getCartId())
                .forEach(item -> shoppingCartRepository.removeBook(cart.getCartId(), item.getIsbn()));
    }

}
