package com.bem.onlineshopping.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class CustomerDTO extends RepresentationModel<CustomerDTO> {
    private Long customerId;
    @NotNull(message = "Customer name cannot be null")
    @NotEmpty(message = "Customer name cannot be empty")
    private String customerName;
    @Email(message = "Email invalid")
    @NotNull(message = "Email cannot be null")
    private String email;
    @NotNull(message = "Password cannot be null")
    @Size(min = 6, max = 30, message = "Password must be exactly 6 digits")
    private String password;
    private CartDTO cart;
    //private List<OrderDTO> orders = new ArrayList<>();
}
