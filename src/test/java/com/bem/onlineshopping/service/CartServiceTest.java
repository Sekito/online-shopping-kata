package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.AddProductToCartDTO;
import com.bem.onlineshopping.exception.BadRequestException;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.model.*;
import com.bem.onlineshopping.repository.CartProductRepository;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    private Cart cart;
    private Product product;

    @BeforeEach
    public void setUp() {
        // Initialize Customer
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        // Initialize Cart
        cart = new Cart();
        cart.setCartId(1L);
        cart.setCustomer(customer);
        cart.setCartProductList(new ArrayList<>()); // Initialize with an empty list

        // Initialize Product
        product = new Product();
        product.setProductId(1L);
        product.setInventory(10);
    }

    @Test
    public void testAddProductToCart_Success() {
        AddProductToCartDTO dto = new AddProductToCartDTO(1L,1L,2);

        when(cartRepository.findByCustomer_CustomerId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Cart updatedCart = cartService.addProductToCart(dto);

        assertNotNull(updatedCart);
        assertEquals(1, updatedCart.getCartProductList().size());
        assertEquals(2, updatedCart.getCartProductList().get(0).getQuantity());
        assertEquals(8, product.getInventory());
        verify(cartRepository).save(cart);
        verify(productRepository).save(product);
    }

    @Test
    public void testAddProductToCart_InsufficientQuantity() {
        product.setInventory(1);
        AddProductToCartDTO dto = new AddProductToCartDTO(1L,1L,2);

        when(cartRepository.findByCustomer_CustomerId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            cartService.addProductToCart(dto);
        });

        assertEquals("Insufficient Quantity", exception.getMessage());
    }

    @Test
    public void testRemoveProductFromCart_Success() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setQuantity(2);
        cart.addCartProduct(cartProduct);

        when(cartProductRepository.findById(cartProduct.getCartProductId())).thenReturn(Optional.of(cartProduct));

        Cart updatedCart = cartService.removeProductFromCart(cartProduct.getCartProductId());

        assertNotNull(updatedCart);
        assertEquals(0, updatedCart.getCartProductList().size());
        assertEquals(12, product.getInventory());
        verify(cartProductRepository).delete(cartProduct);
        verify(cartRepository).save(updatedCart);
    }


    @Test
    public void testGetCartById_Success() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        Cart foundCart = cartService.getCartById(1L);

        assertNotNull(foundCart);
        assertEquals(cart.getCartId(), foundCart.getCartId());
    }

    @Test
    public void testGetCartById_NotFound() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getCartById(1L);
        });

        assertEquals("Cart not found", exception.getMessage());
    }
}
