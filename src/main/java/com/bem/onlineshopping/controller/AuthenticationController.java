package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.CustomerDTO;
import com.bem.onlineshopping.dto.LoginDTO;
import com.bem.onlineshopping.dto.LoginResponse;
import com.bem.onlineshopping.dto.SignupDTO;
import com.bem.onlineshopping.mappers.CustomerMapper;
import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.security.JwtService;
import com.bem.onlineshopping.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Authentication", description = "Operations related to Authentication")
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private final CustomerMapper customerMapper;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, CustomerMapper customerMapper) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.customerMapper = customerMapper;
    }

    @Operation(summary = "User signup", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid signup data")
    })
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CustomerDTO>> register(@Parameter(description = "Signup details for the new user", required = true)
                                                             @Valid @RequestBody SignupDTO signupDTO) {
        Customer registeredUser = authenticationService.signup(signupDTO);

        EntityModel<CustomerDTO> resource = EntityModel.of(customerMapper.toDto(registeredUser));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).register(signupDTO)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).authenticate(new LoginDTO())).withRel("login"));

        return ResponseEntity.ok(resource);
    }


    @Operation(summary = "User login", description = "Authenticate a user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid login credentials")
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<LoginResponse>> authenticate(@Parameter(description = "Login details for the user", required = true)
                                                                   @Valid @RequestBody LoginDTO loginDto) {
        Customer authenticatedUser = authenticationService.authenticate(loginDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        EntityModel<LoginResponse> resource = EntityModel.of(loginResponse);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).authenticate(loginDto)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).authenticatedUser()).withRel("me"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Get authenticated user info", description = "Retrieve the details of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authenticated user details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CustomerDTO>> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer currentUser = (Customer) authentication.getPrincipal();

        EntityModel<CustomerDTO> resource = EntityModel.of(customerMapper.toDto(currentUser));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).authenticatedUser()).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).register(new SignupDTO())).withRel("signup"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).authenticate(new LoginDTO())).withRel("login"));

        return ResponseEntity.ok(resource);
    }
}

