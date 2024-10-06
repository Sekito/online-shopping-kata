package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.AddProductToCartDTO;
import com.bem.onlineshopping.dto.CartDTO;
import com.bem.onlineshopping.dto.CartProductDTO;
import com.bem.onlineshopping.exception.AccessDeniedException;
import com.bem.onlineshopping.mappers.CartMapper;
import com.bem.onlineshopping.mappers.CartProductMapper;
import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.CartProduct;
import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.service.CartService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/carts")
@Tag(name = "Cart Management", description = "Operations related to cart management")
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

    public ResponseEntity<EntityModel<CartDTO>> getCartById( @PathVariable Long cartId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();

        Cart cart = cartService.getCartById(cartId);

        if (!cart.getCustomer().getCustomerId().equals(currentUserId)) {
            throw new AccessDeniedException("Access to the cart is denied for user ID: " + currentUserId);
        }

        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cartId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartByCustomerId(cart.getCustomer().getCustomerId())).withRel("customerCart"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).addProductToCart(null,null)).withRel("addProduct"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).removeProductFromCart(cart.getCartId())).withRel("removeProduct"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartDTO>> getCartByCustomerId(@PathVariable Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();
        if (!customerId.equals(currentUserId)) {
            throw new AccessDeniedException("Access to the cart is denied for user ID: " + currentUserId);
        }

        Cart cart = cartService.getCartByCustomerId(customerId);


        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartByCustomerId(customerId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cart.getCartId())).withRel("cartDetails"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/cart-product/{cartProductId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartProductDTO>> getCartProductById(@PathVariable("cartProductId") Long cartProductId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();
        CartProduct cartProduct = cartService.getCartProductById(cartProductId);

        if (!cartProduct.getCart().getCustomer().getCustomerId().equals(currentUserId)) {
            throw new AccessDeniedException("Get Cart product is denied for user ID: " + currentUserId);
        }

        CartProductDTO cartProductDTO = cartProductMapper.toDto(cartProduct);

        EntityModel<CartProductDTO> resource = EntityModel.of(cartProductDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartProductById(cartProductId)).withSelfRel());

        return ResponseEntity.ok(resource);
    }


    @PostMapping(value = "/add/{productId}/{quantity}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartDTO>> addProductToCart(@PathVariable  @NotNull(message = "productId is null") Long productId,
                                                                 @PathVariable  @Min(value = 1, message = "quantity is less than 1") Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();

        /*Cart cart = cartService.getCartByCustomerId(currentUserId);

        if (cart.getCustomer().getCustomerId().equals(currentUserId)) {
            throw new AccessDeniedException("add Product To Cart is denied for user ID: " + currentUserId);
        }*/

        AddProductToCartDTO addProductToCartDTO = new AddProductToCartDTO(currentUserId,productId,quantity);
        Cart cart = cartService.addProductToCart(addProductToCartDTO);
        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).addProductToCart(productId,quantity)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cart.getCartId())).withRel("cartDetails"));

        return ResponseEntity.ok(resource);
    }


    @DeleteMapping(value = "/remove/{cartProductId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartDTO>> removeProductFromCart(@PathVariable("cartProductId") Long cartProductId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();

        Cart cart = cartService.removeProductFromCart(cartProductId);
        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).removeProductFromCart(cartProductId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cart.getCartId())).withRel("cartDetails"));

        return ResponseEntity.ok(resource);
    }

    @Autowired
    CartRepository cartRepository;

    @GetMapping("/all")

    public ResponseEntity<CollectionModel<CartDTO>> getAllCart() {


        List<Cart> carts = cartRepository.findAll();
        List<CartDTO> cartDTO = carts.stream().map(cartMapper::toDto).collect(Collectors.toList());

        CollectionModel<CartDTO> collectionModel = CollectionModel.of(cartDTO);

        return ResponseEntity.ok(collectionModel);
    }
}
