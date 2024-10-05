package com.bem.onlineshopping.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    @NotNull(message = "Product name cannot be null")
    private String name;
    @Positive(message = "Price must be positive")
    private double price;
    @Min(value = 0, message = "Inventory cannot be negative")
    private int inventory;
}
