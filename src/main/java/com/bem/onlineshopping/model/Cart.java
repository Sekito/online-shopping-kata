package com.bem.onlineshopping.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;


@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    @Positive(message = "Total price must be positive")
    private Double totalPrice;

    @PositiveOrZero(message = "Number of products cannot be negative")
    private Integer numberOfProduct;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    private List<CartProduct> cartProductList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="customerId")
    @JsonIgnore
    private Customer customer;
}
