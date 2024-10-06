package com.bem.onlineshopping.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    @PositiveOrZero(message = "Total price must be positive")
    @Transient
    private Double totalPrice;

    @PositiveOrZero(message = "Number of products cannot be negative")
    @Transient
    private Integer numberOfProduct;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    private List<CartProduct> cartProductList;

    @OneToOne(mappedBy = "cart")
    @JsonIgnore
    @JoinColumn(name = "customerId",referencedColumnName = "customerId")
    private Customer customer;

    public void addCartProduct(CartProduct cartProduct) {
        if (cartProduct != null) {
            cartProduct.setCart(this);
            cartProductList.add(cartProduct);
            updateCartDetails();
        }
    }

    public void removeCartProduct(CartProduct cartProduct) {
        if (cartProduct != null && cartProductList.remove(cartProduct)) {
            cartProduct.setCart(null);
            updateCartDetails();
        }
    }

    private void updateCartDetails() {
        totalPrice = cartProductList.stream()
                .mapToDouble(CartProduct::getTotalPrice)
                .sum();
        numberOfProduct = cartProductList.size();
    }

    public void clearCart() {
        for (CartProduct cartProduct : new ArrayList<>(cartProductList)) {
            removeCartProduct(cartProduct);
        }
    }

    @PostLoad
    private void calculateDerivedFields() {
        updateCartDetails();
    }
}
