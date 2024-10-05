package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.dto.OrderTrackingResponseDTO;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.mappers.OrderMapper;
import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.CartProduct;
import com.bem.onlineshopping.model.Order;
import com.bem.onlineshopping.model.OrderStatusEnum;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.repository.CustomerRepository;
import com.bem.onlineshopping.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderMapper orderMapper;


    @Transactional
    public OrderDTO confirmOrder(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        List<CartProduct> cartProducts = new ArrayList<>();

        cart.getCartProductList().forEach(cartProduct -> {

            CartProduct orderCartProduct = new CartProduct();
            orderCartProduct.setProduct(cartProduct.getProduct());
            orderCartProduct.setQuantity(cartProduct.getQuantity());

            orderCartProduct.setOrder(order);

            cartProducts.add(orderCartProduct);
        });
        order.setOrderCreatedAt(LocalDate.now());
        order.setCartProductList(cartProducts);
        order.setOrderStatus(OrderStatusEnum.NOT_SHIPPED);
        order.setOrderStatusDate(LocalDate.now());
        order.setTrackingNumber(UUID.randomUUID().toString());
        order.setTotalAmount(cart.getTotalPrice());
        order.setTotalItems(cart.getNumberOfProduct());
        order.setDeliveryDate(LocalDate.now().plusDays(3));
        orderRepository.save(order);

        // cart.getCartProductList().clear();
        cartRepository.save(cart);

        return orderMapper.toDto(order);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long orderId) {
        return orderMapper.toDto(orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return orderRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderTrackingResponseDTO trackOrder(Long orderId) {
        OrderDTO order = getOrderById(orderId);
        OrderTrackingResponseDTO response = orderMapper.toTrackingResponseDTO(order);
        return response;
    }
}
