package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.AddProductToCartDTO;
import com.bem.onlineshopping.dto.CartDTO;
import com.bem.onlineshopping.dto.CartProductDTO;
import com.bem.onlineshopping.mappers.CartMapper;
import com.bem.onlineshopping.mappers.CartProductMapper;
import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.CartProduct;
import com.bem.onlineshopping.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CartProductMapper cartProductMapper;

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Long cartId) {
        Cart cart = cartService.getCartById(cartId);
        CartDTO cartDTO = cartMapper.toDto(cart);
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartDTO> getCartByCustomerId(@PathVariable Long customerId) {
        Cart cart = cartService.getCartByCustomerId(customerId);
        CartDTO cartDTO = cartMapper.toDto(cart);
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/card-product/{cartProductId}")
    public ResponseEntity<CartProductDTO> getCartProductById(@PathVariable Long cartProductId) {
        CartProduct cartProduct = cartService.getCartProductById(cartProductId);
        CartProductDTO cartProductDTO = cartProductMapper.toDto(cartProduct);
        return ResponseEntity.ok(cartProductDTO);
    }


    @PostMapping("/add")
    public ResponseEntity<CartDTO> addProductToCart(@Valid @RequestBody AddProductToCartDTO dto){
        Cart cart = cartService.addProductToCart(dto);
        CartDTO cartDTO = cartMapper.toDto(cart);
        return  ResponseEntity.ok(cartDTO);
    }


    @DeleteMapping("/remove/{cartProductId}")
    public ResponseEntity<CartDTO> removeProductFromCart(@PathVariable("cartProductId") Long cartProductId){
        Cart cart = cartService.removeProductFromCart(cartProductId);
        CartDTO cartDTO = cartMapper.toDto(cart);
        return ResponseEntity.ok(cartDTO);
    }
}
