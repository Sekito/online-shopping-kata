package com.bem.onlineshopping.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.mappers.OrderMapper;
import com.bem.onlineshopping.model.*;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.repository.CustomerRepository;
import com.bem.onlineshopping.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapper orderMapper;

    private Cart cart;
    private Product product;
    private CartProduct cartProduct;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test objects
        cart = new Cart();
        product = new Product();
        cartProduct = new CartProduct();

        // Set up relationships
        cartProduct.setProduct(product);
        cartProduct.setQuantity(2);
        cart.addCartProduct(cartProduct);
        cart.setTotalPrice(100.0);
        cart.setNumberOfProduct(2);
        cart.setCustomer(new Customer()); // Assuming a Customer object is set
    }

    @Test
    public void confirmOrder_ShouldCreateOrder() {
        Long cartId = 1L;

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArguments()[0]); // Mock save

        Order order = orderService.confirmOrder(cartId);

        assertNotNull(order);
        assertEquals(cart.getCustomer(), order.getCustomer());
        assertEquals(cart.getTotalPrice(), order.getTotalAmount());
        assertEquals(cart.getNumberOfProduct(), order.getTotalItems());
        assertEquals(OrderStatusEnum.NOT_SHIPPED, order.getOrderStatus());
        assertNotNull(order.getTrackingNumber());
        assertEquals(LocalDate.now().plusDays(3), order.getDeliveryDate());
        assertEquals(1, order.getCartProductList().size());
        assertEquals(cartProduct.getQuantity(), order.getCartProductList().get(0).getQuantity());
        assertEquals(product, order.getCartProductList().get(0).getProduct());
    }

    @Test
    public void confirmOrder_ShouldThrowResourceNotFoundException() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.confirmOrder(cartId);
        });
    }

    @Test
    public void getAllOrders_ShouldReturnListOfOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDTO());

        List<OrderDTO> orderDTOs = orderService.getAllOrders();

        assertNotNull(orderDTOs);
        assertEquals(1, orderDTOs.size());
    }

    @Test
    public void getOrderById_ShouldReturnOrder() {
        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(orderId);

        assertNotNull(foundOrder);
        assertEquals(order, foundOrder);
    }

    @Test
    public void getOrderById_ShouldThrowResourceNotFoundException() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });
    }

    @Test
    public void getOrdersByCustomerId_ShouldReturnListOfOrders() {
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new Customer()));
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        when(orderRepository.findByCustomer_CustomerId(customerId)).thenReturn(orders);

        List<Order> foundOrders = orderService.getOrdersByCustomerId(customerId);

        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
    }

    @Test
    public void getOrdersByCustomerId_ShouldThrowResourceNotFoundException() {
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrdersByCustomerId(customerId);
        });
    }

    @Test
    public void trackOrder_ShouldReturnOrder() {
        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.trackOrder(orderId);

        assertNotNull(foundOrder);
        assertEquals(order, foundOrder);
    }

    @Test
    public void trackOrder_ShouldThrowResourceNotFoundException() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.trackOrder(orderId);
        });
    }
}
