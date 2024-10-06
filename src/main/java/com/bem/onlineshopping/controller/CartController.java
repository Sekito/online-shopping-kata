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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final CartProductMapper cartProductMapper;

    @Autowired
    public CartController(CartService cartService,
                          CartMapper cartMapper,
                          CartProductMapper cartProductMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
        this.cartProductMapper = cartProductMapper;
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<EntityModel<CartDTO>> getCartById(@PathVariable Long cartId) {
        Cart cart = cartService.getCartById(cartId);
        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cartId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartByCustomerId(cart.getCustomer().getCustomerId())).withRel("customerCart"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).addProductToCart(null)).withRel("addProduct"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).removeProductFromCart(cart.getCartId())).withRel("removeProduct"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<EntityModel<CartDTO>> getCartByCustomerId(@PathVariable Long customerId) {
        Cart cart = cartService.getCartByCustomerId(customerId);
        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartByCustomerId(customerId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cart.getCartId())).withRel("cartDetails"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/cart-product/{cartProductId}")
    public ResponseEntity<EntityModel<CartProductDTO>> getCartProductById(@PathVariable("cartProductId") Long cartProductId) {
        CartProduct cartProduct = cartService.getCartProductById(cartProductId);
        CartProductDTO cartProductDTO = cartProductMapper.toDto(cartProduct);

        EntityModel<CartProductDTO> resource = EntityModel.of(cartProductDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartProductById(cartProductId)).withSelfRel());

        return ResponseEntity.ok(resource);
    }


    @PostMapping("/add")
    public ResponseEntity<EntityModel<CartDTO>> addProductToCart(@Valid @RequestBody AddProductToCartDTO dto) {
        Cart cart = cartService.addProductToCart(dto);
        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).addProductToCart(dto)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cart.getCartId())).withRel("cartDetails"));

        return ResponseEntity.ok(resource);
    }


    @DeleteMapping("/remove/{cartProductId}")
    public ResponseEntity<EntityModel<CartDTO>> removeProductFromCart(@PathVariable("cartProductId") Long cartProductId) {
        Cart cart = cartService.removeProductFromCart(cartProductId);
        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).removeProductFromCart(cartProductId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cart.getCartId())).withRel("cartDetails"));

        return ResponseEntity.ok(resource);
    }
}
