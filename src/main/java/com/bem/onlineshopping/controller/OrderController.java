package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.dto.OrderTrackingResponseDTO;

import com.bem.onlineshopping.mappers.OrderMapper;
import com.bem.onlineshopping.model.Order;
import com.bem.onlineshopping.model.OrderStatusEnum;
import com.bem.onlineshopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping("/confirm/{cartId}")
    public ResponseEntity<EntityModel<OrderDTO>> confirmOrder(@PathVariable Long cartId) {
        Order order = orderService.confirmOrder(cartId);
        OrderDTO orderDTO = orderMapper.toDto(order);

        EntityModel<OrderDTO> resource = EntityModel.of(orderDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).confirmOrder(cartId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderById(order.getOrderId())).withRel("orderDetails"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderByCustomerId(order.getCustomer().getCustomerId())).withRel("customerOrders"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<EntityModel<OrderDTO>> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        OrderDTO orderDTO = orderMapper.toDto(order);

        EntityModel<OrderDTO> resource = EntityModel.of(orderDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderById(orderId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).trackOrder(orderId)).withRel("trackOrder"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderByCustomerId(order.getCustomer().getCustomerId())).withRel("customerOrders"));

        return ResponseEntity.ok(resource);
    }


   /* @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> orderDTOs = orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusEnum> getOrderStatus(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO.getOrderStatus());
    }
    */

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<EntityModel<OrderDTO>>> getOrderByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);

        List<EntityModel<OrderDTO>> orderDTOs = orders.stream()
                .map(order -> {
                    OrderDTO orderDTO = orderMapper.toDto(order);
                    EntityModel<OrderDTO> resource = EntityModel.of(orderDTO);
                    resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderById(order.getOrderId())).withSelfRel());
                    return resource;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/track/{orderId}")
    public ResponseEntity<EntityModel<OrderTrackingResponseDTO>> trackOrder(@PathVariable("orderId") Long orderId) {
        Order order = orderService.trackOrder(orderId);
        OrderTrackingResponseDTO response = orderMapper.toTrackingResponseDTO(order);

        EntityModel<OrderTrackingResponseDTO> resource = EntityModel.of(response);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).trackOrder(orderId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderById(order.getOrderId())).withRel("orderDetails"));

        return ResponseEntity.ok(resource);
    }
}
