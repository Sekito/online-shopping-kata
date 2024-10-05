package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.AddProductToCartDTO;
import com.bem.onlineshopping.dto.CartDTO;
import com.bem.onlineshopping.dto.CartProductDTO;
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

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCartById(cartId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartDTO> getCartByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCartByCustomerId(customerId));
    }

    @GetMapping("/card-product/{cartProductId}")
    public ResponseEntity<CartProductDTO> getCartProductById(@PathVariable Long cartProductId) {
        return ResponseEntity.ok(cartService.getCartProductById(cartProductId));
    }


    @PostMapping("/add")
    public ResponseEntity<CartDTO> addProductToCart(@Valid @RequestBody AddProductToCartDTO dto){

        return  ResponseEntity.ok(cartService.addProductToCart(dto));
    }


    @DeleteMapping("/remove/{cartProductId}")
    public ResponseEntity<CartDTO> removeProductFromCart(@PathVariable("cartProductId") Long cartProductId){
        return ResponseEntity.ok(cartService.removeProductFromCart(cartProductId));
    }
}
