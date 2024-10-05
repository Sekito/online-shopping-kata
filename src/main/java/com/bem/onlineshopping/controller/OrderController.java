package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.dto.OrderTrackingResponseDTO;

import com.bem.onlineshopping.model.OrderStatusEnum;
import com.bem.onlineshopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/confirm/{cartId}")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable Long cartId) {
        return ResponseEntity.ok(orderService.confirmOrder(cartId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusEnum> getOrderStatus(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO.getOrderStatus());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrderByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/track/{orderId}")
    public ResponseEntity<OrderTrackingResponseDTO> trackOrder(@PathVariable("orderId") Long orderId) {
        OrderTrackingResponseDTO response = orderService.trackOrder(orderId);
        return ResponseEntity.ok(response);
    }
}
