package com.bem.onlineshopping.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bem.onlineshopping.dto.AddProductToCartDTO;
import com.bem.onlineshopping.exception.BadRequestException;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.CartProduct;
import com.bem.onlineshopping.model.Product;
import com.bem.onlineshopping.repository.CartProductRepository;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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
        cart.addCartProduct(cartProduct);
    }

    @Test
    public void getCartById_ShouldReturnCart() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Cart foundCart = cartService.getCartById(cartId);
        assertNotNull(foundCart);
        assertEquals(cart, foundCart);
    }

    @Test
    public void getCartById_ShouldThrowResourceNotFoundException() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getCartById(cartId);
        });
    }

    @Test
    public void addProductToCart_ShouldAddProductToCart() {
        AddProductToCartDTO dto = new AddProductToCartDTO();
        dto.setCartId(cart.getCartId());
        dto.setProductId(product.getProductId());
        dto.setQuantity(1);

        product.setInventory(5);

        when(cartRepository.findById(dto.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(dto.getProductId())).thenReturn(Optional.of(product));

        Cart updatedCart = cartService.addProductToCart(dto);
        assertEquals(1, updatedCart.getCartProductList().size());
        assertEquals(1, updatedCart.getCartProductList().get(0).getQuantity());
        assertEquals(4, product.getInventory());
    }

    @Test
    public void addProductToCart_ShouldThrowInsufficientQuantityException() {
        AddProductToCartDTO dto = new AddProductToCartDTO();
        dto.setCartId(cart.getCartId());
        dto.setProductId(product.getProductId());
        dto.setQuantity(10);

        product.setInventory(5);

        when(cartRepository.findById(dto.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(dto.getProductId())).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () -> {
            cartService.addProductToCart(dto);
        });
    }

    @Test
    public void removeProductFromCart_ShouldRemoveProductFromCart() {
        Long cartProductId = cartProduct.getCartProductId();

        when(cartProductRepository.findById(cartProductId)).thenReturn(Optional.of(cartProduct));
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart updatedCart = cartService.removeProductFromCart(cartProductId);
        assertEquals(0, updatedCart.getCartProductList().size());
        assertEquals(1, product.getInventory());
    }

    @Test
    public void removeProductFromCart_ShouldThrowRuntimeException() {
        Long cartProductId = 1L;
        when(cartProductRepository.findById(cartProductId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            cartService.removeProductFromCart(cartProductId);
        });
    }

    @Test
    public void getCartByCustomerId_ShouldReturnCart() {
        Long customerId = 1L;
        when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.of(cart));

        Cart foundCart = cartService.getCartByCustomerId(customerId);
        assertNotNull(foundCart);
        assertEquals(cart, foundCart);
    }

    @Test
    public void getCartByCustomerId_ShouldThrowResourceNotFoundException() {
        Long customerId = 1L;
        when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getCartByCustomerId(customerId);
        });
    }

    @Test
    public void getCartProductById_ShouldReturnCartProduct() {
        Long cartProductId = cartProduct.getCartProductId();
        when(cartProductRepository.findById(cartProductId)).thenReturn(Optional.of(cartProduct));

        CartProduct foundCartProduct = cartService.getCartProductById(cartProductId);
        assertNotNull(foundCartProduct);
        assertEquals(cartProduct, foundCartProduct);
    }

    @Test
    public void getCartProductById_ShouldThrowResourceNotFoundException() {
        Long cartProductId = 1L;
        when(cartProductRepository.findById(cartProductId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getCartProductById(cartProductId);
        });
    }
}
