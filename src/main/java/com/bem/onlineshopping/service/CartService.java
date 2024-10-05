package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.AddProductToCartDTO;
import com.bem.onlineshopping.dto.CartDTO;
import com.bem.onlineshopping.dto.CartProductDTO;
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
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CartProductMapper cartProductMapper;

    @Autowired
    private CartProductRepository cartProductRepository;


    public CartDTO getCartById(Long cartId) {
        return cartMapper.toDto(cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found")));
    }


    @Transactional
    public CartDTO addProductToCart(AddProductToCartDTO dto) {
        Cart cart = cartRepository.findById(dto.getCartId())
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
            cartProduct.setCart(cart);
            cartProduct.setProduct(product);
            cartProduct.setQuantity(dto.getQuantity());
            cartProductRepository.save(cartProduct);
            cart.getCartProductList().add(cartProduct);
        }

        product.setInventory(availableQuantity - dto.getQuantity());
        productRepository.save(product);

        cart.setNumberOfProduct(cart.getCartProductList().size());
        double totalPrice = cart.getCartProductList().stream()
                .map(cartProduct1 -> cartProduct1.getProduct().getPrice() * cartProduct1.getQuantity())
                .reduce(0.0, Double::sum);
        cart.setTotalPrice(totalPrice);

        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    public CartDTO removeProductFromCart(Long cartProductId) {
        CartProduct cartProduct = cartProductRepository.findById(cartProductId).orElseThrow(() -> new RuntimeException("Product not found"));


        Product product = cartProduct.getProduct();
        product.setInventory(product.getInventory()+cartProduct.getQuantity());

        productRepository.save(product);
        cartProductRepository.delete(cartProduct);
        Cart cart = cartProduct.getCart();
        double sum = cart.getCartProductList().stream()
                .map(cartProduct1 -> cartProduct1.getProduct().getPrice() * cartProduct1.getQuantity())
                .reduce((Double::sum) ).get();
        cart.setTotalPrice(sum);
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public CartDTO getCartByCustomerId(Long customerId) {
        return cartMapper.toDto(cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found")));
    }

    public CartProductDTO getCartProductById(Long cartProductId) {
        CartProductDTO cartProductDTO = cartProductMapper.toDto(cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found")));
        return cartProductDTO;
    }
}
