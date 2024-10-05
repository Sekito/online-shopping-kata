package com.bem.onlineshopping.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customerId;

    @NotNull(message = "name cannot set as null")
    @NotEmpty(message =  "name cannot set as empty")
    private String customerName;

    @Column(unique = true)
    @Email(message = "email is invalid")
    private String email;

    @NotNull(message = "password cannot set as null")
    @Size(min = 6 ,max = 30 ,message = "password is Must Be 6 digit")
    private String password;


    @OneToOne(mappedBy = "customer",cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
