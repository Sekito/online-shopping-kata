package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.dto.OrderTrackingResponseDTO;
import com.bem.onlineshopping.exception.BadRequestException;
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

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    private static final int DELIVERY_DAYS = 3;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        CustomerRepository customerRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }


    @Transactional
    public Order confirmOrder(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getCartProductList().size() == 0){
            throw new BadRequestException("Cart is empty");
        }

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
        order.setDeliveryDate(LocalDate.now().plusDays(DELIVERY_DAYS));
        orderRepository.save(order);

        cart.clearCart();
        cartRepository.save(cart);

        return order;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        List<Order> orders = orderRepository.findByCustomer_CustomerId(customerId);
        return orders;
    }

    public Order trackOrder(Long orderId) {
        return getOrderById(orderId);
    }

}
