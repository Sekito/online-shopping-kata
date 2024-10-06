package com.bem.onlineshopping.dto;

import com.bem.onlineshopping.model.OrderStatusEnum;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO extends RepresentationModel<OrderDTO> {
    private Long orderId;
    private LocalDate orderCreatedAt;
    private OrderStatusEnum orderStatus;
    private LocalDate orderStatusDate;
    private Long customerId;
    private String trackingNumber;
    private Double totalAmount;
    private Integer totalItems;
    private LocalDate deliveryDate;
    private List<CartProductDTO> cartProductList;
}
