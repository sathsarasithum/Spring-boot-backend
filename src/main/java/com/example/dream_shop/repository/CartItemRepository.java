package com.example.dream_shop.repository;

import com.example.dream_shop.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{
    void deleteAllByCartId(Long id);
}
