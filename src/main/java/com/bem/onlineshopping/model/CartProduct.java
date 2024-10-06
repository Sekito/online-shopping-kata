package com.bem.onlineshopping.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Objects;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    public Double getTotalPrice() {
        if (product != null) {
            return product.getPrice() * quantity;
        }
        return 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartProduct)) return false;
        CartProduct that = (CartProduct) o;
        return Objects.equals(cartProductId, that.cartProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartProductId);
    }
}
