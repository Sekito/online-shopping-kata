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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Order Management", description = "Operations related to Orders management")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderMapper orderMapper, CartService cartService) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.cartService = cartService;
    }

    @Operation(summary = "Confirm an order", description = "Confirm the order associated with a specific cart ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order confirmed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @PostMapping(value = "/confirm/{cartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OrderDTO>> confirmOrder(@Parameter(description = "ID of the cart to confirm the order", required = true)
                                                              @NotNull(message = "cartId is null") @PathVariable Long cartId) {

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


    @Operation(summary = "Get order by ID", description = "Retrieve a specific order using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OrderDTO>> getOrderById(@Parameter(description = "ID of the order to retrieve", required = true)
                                                              @NotNull(message = "orderId is null") @PathVariable Long orderId) {

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


    @Operation(summary = "Get orders by customer ID", description = "Retrieve all orders associated with a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EntityModel<OrderDTO>>> getOrdersByCustomerId(@Parameter(description = "ID of the customer to retrieve orders for", required = true)
                                                                             @NotNull(message = "customerId is null") @PathVariable Long customerId) {

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

    @Operation(summary = "Track an order", description = "Retrieve tracking information for a specific order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order tracking information retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderTrackingResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping(value = "/track/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OrderTrackingResponseDTO>> trackOrder(@Parameter(description = "ID of the order to track", required = true)
                                                                            @NotNull(message = "orderId is null") @PathVariable("orderId") Long orderId) {
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
