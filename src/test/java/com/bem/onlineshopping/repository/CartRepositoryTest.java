package com.bem.onlineshopping.repository;

import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Rollback
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setCustomerName("Test Customer");
        customerRepository.save(customer);
    }

    @Test
    public void findByCustomer_CustomerId_ShouldReturnOrders() {
        Order order1 = new Order();
        order1.setCustomer(customer);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setCustomer(customer);
        orderRepository.save(order2);

        List<Order> foundOrders = orderRepository.findByCustomer_CustomerId(customer.getCustomerId());

        assertThat(foundOrders).hasSize(2);
        assertThat(foundOrders).containsExactlyInAnyOrder(order1, order2);
    }

    @Test
    public void findByCustomer_CustomerId_ShouldReturnEmptyListWhenNoOrders() {
        List<Order> foundOrders = orderRepository.findByCustomer_CustomerId(customer.getCustomerId());

        assertThat(foundOrders).isEmpty();
    }

    @Test
    public void findByCustomer_CustomerId_ShouldReturnEmptyListForNonExistentCustomer() {
        Long nonExistentCustomerId = 999L;

        List<Order> foundOrders = orderRepository.findByCustomer_CustomerId(nonExistentCustomerId);

        assertThat(foundOrders).isEmpty();
    }
}
