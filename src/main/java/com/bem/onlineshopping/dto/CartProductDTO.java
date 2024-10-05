package com.bem.onlineshopping.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartProductDTO {
    private Long cartProductId;
    @NotNull(message = "Product cannot be null")
    private ProductDTO product;
    @Min(value = 1, message = "Quantity cannot be less than 1")
    private Integer quantity;
}
