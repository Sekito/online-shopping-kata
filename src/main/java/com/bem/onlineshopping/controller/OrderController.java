package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.dto.OrderTrackingResponseDTO;

import com.bem.onlineshopping.mappers.OrderMapper;
import com.bem.onlineshopping.model.Order;
import com.bem.onlineshopping.model.OrderStatusEnum;
import com.bem.onlineshopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping("/confirm/{cartId}")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable Long cartId) {
        Order order = orderService.confirmOrder(cartId);
        OrderDTO orderDTO = orderMapper.toDto(order);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.confirmOrder(orderId);
        OrderDTO orderDTO = orderMapper.toDto(order);
        return ResponseEntity.ok(orderDTO);
    }

    /*@GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusEnum> getOrderStatus(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO.getOrderStatus());
    }*/

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrderByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);

        List<OrderDTO> orderDTOs =  orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/track/{orderId}")
    public ResponseEntity<OrderTrackingResponseDTO> trackOrder(@PathVariable("orderId") Long orderId) {
        Order order =  orderService.trackOrder(orderId);
        OrderTrackingResponseDTO response = orderMapper.toTrackingResponseDTO(order);
        return ResponseEntity.ok(response);
    }
}
