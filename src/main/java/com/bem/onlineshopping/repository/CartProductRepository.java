package com.bem.onlineshopping.repository;

import com.bem.onlineshopping.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
}
