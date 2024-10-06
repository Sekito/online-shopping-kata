package com.bem.onlineshopping.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class LoginResponse extends RepresentationModel<LoginResponse> {
    private String token;
    private long expiresIn;
}
