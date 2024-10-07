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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get cart by ID", description = "Retrieve a specific cart using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping(value = "/{cartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartDTO>> getCartById(@Parameter(description = "ID of the cart to retrieve", required = true)
                                                            @NotNull(message = "cartId is null") @PathVariable Long cartId) {
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
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).addProductToCart(null, null)).withRel("addProduct"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).removeProductFromCart(cart.getCartId())).withRel("removeProduct"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Get cart by customer ID", description = "Retrieve the cart associated with a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartDTO>> getCartByCustomerId(@Parameter(description = "ID of the customer whose cart to retrieve", required = true)
                                                                    @NotNull(message = "customerId is null") @PathVariable Long customerId) {
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

    @Operation(summary = "Get cart product by ID", description = "Retrieve a specific cart product using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart product retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartProductDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping(value = "/cart-product/{cartProductId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartProductDTO>> getCartProductById(@Parameter(description = "ID of the cart product to retrieve", required = true)
                                                                          @NotNull(message = "cartProductId is null") @PathVariable("cartProductId") Long cartProductId) {

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


    @Operation(summary = "Add product to cart", description = "Add a specific product to the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to cart successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping(value = "/add/{productId}/{quantity}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartDTO>> addProductToCart(@Parameter(description = "ID of the product to add", required = true)
                                                                 @PathVariable @NotNull(message = "productId is null") Long productId,
                                                                 @Parameter(description = "Quantity of the product to add", required = true)
                                                                 @PathVariable @Min(value = 1, message = "quantity is less than 1") Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((Customer) authentication.getPrincipal()).getCustomerId();

        AddProductToCartDTO addProductToCartDTO = new AddProductToCartDTO(currentUserId, productId, quantity);
        Cart cart = cartService.addProductToCart(addProductToCartDTO);
        CartDTO cartDTO = cartMapper.toDto(cart);

        EntityModel<CartDTO> resource = EntityModel.of(cartDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).addProductToCart(productId, quantity)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CartController.class).getCartById(cart.getCartId())).withRel("cartDetails"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Remove product from cart", description = "Remove a specific product from the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product removed from cart successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping(value = "/remove/{cartProductId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CartDTO>> removeProductFromCart(@Parameter(description = "ID of the cart product to remove", required = true)
                                                                      @NotNull(message = "cartProductId is null") @PathVariable("cartProductId") Long cartProductId) {
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
