package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.LoginDTO;
import com.bem.onlineshopping.dto.LoginResponse;
import com.bem.onlineshopping.dto.SignupDTO;
import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.security.JwtService;
import com.bem.onlineshopping.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Customer> register(@RequestBody SignupDTO signupDTO) {
        Customer registeredUser = authenticationService.signup(signupDTO);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDTO loginDto) {
        Customer authenticatedUser = authenticationService.authenticate(loginDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<Customer> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Customer currentUser = (Customer) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }
}
