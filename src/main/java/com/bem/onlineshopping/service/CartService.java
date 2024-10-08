package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.AddProductToCartDTO;
import com.bem.onlineshopping.dto.CartDTO;
import com.bem.onlineshopping.dto.CartProductDTO;
import com.bem.onlineshopping.exception.AccessDeniedException;
import com.bem.onlineshopping.exception.BadRequestException;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.mappers.CartMapper;
import com.bem.onlineshopping.mappers.CartProductMapper;
import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.CartProduct;
import com.bem.onlineshopping.model.Product;
import com.bem.onlineshopping.repository.CartProductRepository;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;


    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       CartProductRepository cartProductRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartProductRepository = cartProductRepository;
    }


    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    @Transactional
    public Cart addProductToCart(AddProductToCartDTO dto) {
        Cart cart = cartRepository.findByCustomer_CustomerId(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        int availableQuantity = product.getInventory();

        if (availableQuantity < dto.getQuantity()) {
            throw new BadRequestException("Insufficient Quantity");
        }

        Optional<CartProduct> existingCartProductOpt = cart.getCartProductList().stream()
                .filter(cartProduct -> cartProduct.getProduct().getProductId().equals(product.getProductId()))
                .findFirst();

        if (existingCartProductOpt.isPresent()) {
            CartProduct existingCartProduct = existingCartProductOpt.get();
            int newQuantity = existingCartProduct.getQuantity() + dto.getQuantity();
            existingCartProduct.setQuantity(newQuantity);
            cartProductRepository.save(existingCartProduct);
        } else {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setProduct(product);
            cartProduct.setQuantity(dto.getQuantity());
            cart.addCartProduct(cartProduct);
            cartProductRepository.save(cartProduct);
        }

        product.setInventory(availableQuantity - dto.getQuantity());
        productRepository.save(product);

        cartRepository.save(cart);

        return cart;
    }

    @Transactional
    public Cart removeProductFromCart(Long cartProductId) {

        CartProduct cartProduct = cartProductRepository.findById(cartProductId).orElseThrow(() -> new ResourceNotFoundException("CartProduct not found"));


        Product product = cartProduct.getProduct();
        product.setInventory(product.getInventory()+cartProduct.getQuantity());


        Cart cart = cartProduct.getCart();
        cart.removeCartProduct(cartProduct);
        cartProductRepository.delete(cartProduct);
        cartRepository.save(cart);
        return cart;
    }

    public Cart getCartByCustomerId(Long customerId) {
        return cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    public CartProduct getCartProductById(Long cartProductId) {
        return cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    public Cart updateProductQuantity(Long cartProductId, Integer quantity) {
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart product not found"));

        Product product = productRepository.findById(cartProduct.getProduct().getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        int availableQuantity = product.getInventory();

        if (availableQuantity < quantity) {
            throw new BadRequestException("Insufficient Quantity");
        }

        cartProduct.setQuantity(quantity);
        cartProduct.getCart().updateCartDetails();

        product.setInventory(availableQuantity - cartProduct.getQuantity());

        cartProductRepository.save(cartProduct);

        return cartProduct.getCart();
    }
}
