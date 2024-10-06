package com.bem.onlineshopping.service;


import com.bem.onlineshopping.dto.LoginDTO;
import com.bem.onlineshopping.dto.SignupDTO;
import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.repository.CustomerRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            CustomerRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.customerRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer signup(SignupDTO input) {
        Customer user = new Customer();
        user.setCustomerName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return customerRepository.save(user);
    }

    public Customer authenticate(LoginDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return customerRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}
