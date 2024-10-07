package com.example.dream_shop.service.cart;

import com.example.dream_shop.exception.ResourceNotFounException;
import com.example.dream_shop.model.Cart;
import com.example.dream_shop.model.CartItem;
import com.example.dream_shop.model.Product;
import com.example.dream_shop.repository.CartItemRepository;
import com.example.dream_shop.repository.CartRepository;
import com.example.dream_shop.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final IProductService productService;
    private final ICartService cartService;
    private final CartRepository cartRepository;


    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        //1. get the cart
        //2. Get the product
        //3. Check if the product already in the cart
        //4. If Yes, then increase the quantity the requested quantity
        //5. If No, the initiate a new CartItem entry

        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());
        if(cartItem.getId() == null){
            cartItem.setCart(cart);
            cartItem.setQuantity(quantity);
            cartItem.setProduct(product);
            cartItem.setUnitPrice(product.getPrice());
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new ResourceNotFounException("Product not found"));
        cart.remove(itemToRemove);
        cartRepository.save(cart);

    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        cart.getCartItems()
                .stream().filter(item -> item.getProduct().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });
//        BigDecimal totalAmount = cart.getCartItems()
//                        .stream().map(CartItem :: getTotalPrice)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = cart.getCartItems()
                .stream().map(CartItem ::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId){
         Cart cart = cartService.getCart(cartId);
         return cart.getCartItems()
                 .stream()
                 .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                 .findFirst().orElseThrow(() ->new ResourceNotFounException("item not found"));
    }
}
