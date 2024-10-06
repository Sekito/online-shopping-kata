package com.bem.onlineshopping.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CartDTO {
    private Long cartId;
    private Double totalPrice;
    private Integer numberOfProduct;
    private List<CartProductDTO> cartProductList;
}