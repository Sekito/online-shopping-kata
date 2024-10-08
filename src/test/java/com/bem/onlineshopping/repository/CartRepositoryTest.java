package com.bem.onlineshopping.repository;

import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartRepositoryTest {

    @Mock
    private CartRepository cartRepository;

    private Cart cart;

    @BeforeEach
    public void setUp() {
        cart = new Cart();
        cart.setCartId(1L);

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        cart.setCustomer(customer);
    }

    @Test
    public void testFindByCustomer_CustomerId_Success() {
        when(cartRepository.findByCustomer_CustomerId(1L)).thenReturn(Optional.of(cart));

        Optional<Cart> foundCart = cartRepository.findByCustomer_CustomerId(1L);

        assertTrue(foundCart.isPresent());
        assertEquals(cart.getCartId(), foundCart.get().getCartId());
        verify(cartRepository).findByCustomer_CustomerId(1L);
    }

    @Test
    public void testFindByCustomer_CustomerId_NotFound() {
        when(cartRepository.findByCustomer_CustomerId(1L)).thenReturn(Optional.empty());

        Optional<Cart> foundCart = cartRepository.findByCustomer_CustomerId(1L);

        assertFalse(foundCart.isPresent());
        verify(cartRepository).findByCustomer_CustomerId(1L);
    }
}
