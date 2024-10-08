package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.exception.BadRequestException;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.mappers.OrderMapper;
import com.bem.onlineshopping.model.*;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.repository.CustomerRepository;
import com.bem.onlineshopping.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    private Customer customer;
    private Order order;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setCustomerId(1L);

        cart = new Cart();
        cart.setCartId(1L);
        cart.setCustomer(customer);
        cart.setCartProductList(new ArrayList<>());

        order = new Order();
        order.setOrderId(1L);
    }

    @Test
    public void testConfirmOrder_Success() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(new Product());
        cartProduct.setQuantity(2);
        cart.getCartProductList().add(cartProduct);
        cart.updateCartDetails();
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order confirmedOrder = orderService.confirmOrder(1L);

        assertNotNull(confirmedOrder);
        assertEquals(OrderStatusEnum.NOT_SHIPPED, confirmedOrder.getOrderStatus());
        assertEquals(cart.getTotalPrice(), confirmedOrder.getTotalAmount());
        assertTrue(cart.getCartProductList().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    public void testConfirmOrder_EmptyCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            orderService.confirmOrder(1L);
        });

        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    public void testGetAllOrders_Success() {
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(new OrderDTO());

        List<OrderDTO> orderDTOs = orderService.getAllOrders();

        assertNotNull(orderDTOs);
        assertEquals(1, orderDTOs.size());
        verify(orderRepository).findAll();
    }

    @Test
    public void testGetOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(order.getOrderId(), foundOrder.getOrderId());
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrderById(1L);
        });

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    public void testGetOrdersByCustomerId_Success() {
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomer_CustomerId(1L)).thenReturn(orders);

        List<Order> foundOrders = orderService.getOrdersByCustomerId(1L);

        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
    }

    @Test
    public void testGetOrdersByCustomerId_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrdersByCustomerId(1L);
        });

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    public void testTrackOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order trackedOrder = orderService.trackOrder(1L);

        assertNotNull(trackedOrder);
        assertEquals(order.getOrderId(), trackedOrder.getOrderId());
    }
}
