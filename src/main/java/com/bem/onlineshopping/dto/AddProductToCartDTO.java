package com.bem.onlineshopping.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
public class AddProductToCartDTO extends RepresentationModel<AddProductToCartDTO> {

    @NotNull(message = "customerId is null")
    private Long customerId;

    @NotNull(message = "productId is null")
    private Long productId;

    @Min(value = 1, message = "quantity is less than 1")
    private Integer quantity;
}
