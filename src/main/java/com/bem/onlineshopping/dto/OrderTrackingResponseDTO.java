package com.bem.onlineshopping.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
public class OrderTrackingResponseDTO extends RepresentationModel<OrderTrackingResponseDTO> {
    private String trackingNumber;
    private String orderStatus;
    private LocalDate orderStatusDate;
    private Double totalAmount;
    private Integer totalItems;
    private LocalDate deliveryDate;
}
