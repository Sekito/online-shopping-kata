package com.bem.onlineshopping.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductToCartDTO {
    @NotNull(message = "cartId is null")
    private Long cartId;

    @NotNull(message = "productId is null")
    private Long productId;

    @Min(value = 1, message = "quantity is less than 1")
    private Integer quantity;
}
