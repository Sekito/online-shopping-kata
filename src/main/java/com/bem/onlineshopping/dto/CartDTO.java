package com.bem.onlineshopping.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Set;

@Data
public class CartDTO extends RepresentationModel<CartDTO> {
    private Long cartId;
    private Double totalPrice;
    private Integer numberOfProduct;
    private List<CartProductDTO> cartProductList;
}