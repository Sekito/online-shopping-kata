package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.CartDTO;
import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.dto.OrderTrackingResponseDTO;

import com.bem.onlineshopping.exception.AccessDeniedException;
import com.bem.onlineshopping.mappers.OrderMapper;
import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.model.Order;
import com.bem.onlineshopping.model.OrderStatusEnum;
import com.bem.onlineshopping.service.CartService;
import com.bem.onlineshopping.service.OrderService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderMapper orderMapper,CartService cartService) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.cartService = cartService;
    }

    @PostMapping(value = "/confirm/{cartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OrderDTO>> confirmOrder(@NotNull @PathVariable Long cartId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();

        Cart cart = cartService.getCartById(cartId);

        if (!cart.getCustomer().getCustomerId().equals(currentUserId)) {
            throw new AccessDeniedException("Confirm Order is denied for user ID: " + currentUserId);
        }

        Order order = orderService.confirmOrder(cartId);
        OrderDTO orderDTO = orderMapper.toDto(order);

        EntityModel<OrderDTO> resource = EntityModel.of(orderDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).confirmOrder(cartId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderById(order.getOrderId())).withRel("orderDetails"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrdersByCustomerId(order.getCustomer().getCustomerId())).withRel("customerOrders"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OrderDTO>> getOrderById(@NotNull @PathVariable Long orderId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();

        Order order = orderService.getOrderById(orderId);

        if (!order.getCustomer().getCustomerId().equals(currentUserId)) {
            throw new AccessDeniedException("Confirm Order is denied for user ID: " + currentUserId);
        }

        OrderDTO orderDTO = orderMapper.toDto(order);

        EntityModel<OrderDTO> resource = EntityModel.of(orderDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderById(orderId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).trackOrder(orderId)).withRel("trackOrder"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrdersByCustomerId(order.getCustomer().getCustomerId())).withRel("customerOrders"));

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

    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EntityModel<OrderDTO>>> getOrdersByCustomerId(@NotNull @PathVariable Long customerId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();

        if (!customerId.equals(currentUserId)) {
            throw new AccessDeniedException("Get Order is denied for user ID: " + currentUserId);
        }

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

    @GetMapping(value = "/track/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OrderTrackingResponseDTO>> trackOrder(@PathVariable("orderId") Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();
        Order order = orderService.getOrderById(orderId);
        if (!order.getCustomer().getCustomerId().equals(currentUserId)) {
            throw new AccessDeniedException("Track Order is denied for user ID: " + currentUserId);
        }
        order = orderService.trackOrder(orderId);
        OrderTrackingResponseDTO response = orderMapper.toTrackingResponseDTO(order);

        EntityModel<OrderTrackingResponseDTO> resource = EntityModel.of(response);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).trackOrder(orderId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrderById(order.getOrderId())).withRel("orderDetails"));

        return ResponseEntity.ok(resource);
    }
}
