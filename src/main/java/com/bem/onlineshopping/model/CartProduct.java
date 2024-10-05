package com.bem.onlineshopping.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Data
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cartProductId;

    @Min(value = 1, message = "Quantity is less than 1")
    private Integer quantity;

    @JsonIgnore
    @ManyToOne
    private Product product;

    @JsonIgnore
    @ManyToOne
    private Cart cart;

    @JsonIgnore
    @ManyToOne
    private Order order;
}
