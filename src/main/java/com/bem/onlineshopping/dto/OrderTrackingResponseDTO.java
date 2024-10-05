package com.bem.onlineshopping.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderTrackingResponseDTO {
    private String trackingNumber;
    private String orderStatus;
    private LocalDate orderStatusDate;
    private Double totalAmount;
    private Integer totalItems;
    private LocalDate deliveryDate;
}
