package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.CustomerDTO;
import com.bem.onlineshopping.dto.LoginDTO;
import com.bem.onlineshopping.dto.LoginResponse;
import com.bem.onlineshopping.dto.SignupDTO;
import com.bem.onlineshopping.mappers.CustomerMapper;
import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.security.JwtService;
import com.bem.onlineshopping.service.AuthenticationService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private final CustomerMapper customerMapper;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService,CustomerMapper customerMapper) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.customerMapper = customerMapper;
    }

    @PostMapping(value = "/signup" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CustomerDTO>> register(@RequestBody SignupDTO signupDTO) {
        Customer registeredUser = authenticationService.signup(signupDTO);

        EntityModel<CustomerDTO> resource = EntityModel.of(customerMapper.toDto(registeredUser));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).register(signupDTO)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthenticationController.class).authenticate(new LoginDTO())).withRel("login"));

        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<LoginResponse>> authenticate(@RequestBody LoginDTO loginDto) {
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

