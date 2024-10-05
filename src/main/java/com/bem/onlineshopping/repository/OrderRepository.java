package com.bem.onlineshopping.repository;

import com.bem.onlineshopping.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
