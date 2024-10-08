package com.bem.onlineshopping.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private String trackingNumber;

    private LocalDate orderCreatedAt;
    private LocalDate deliveryDate;
    private Double totalAmount;
    private Integer totalItems;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Order status cannot be null")
    private OrderStatusEnum orderStatus = OrderStatusEnum.NOT_SHIPPED;
    private LocalDate orderStatusDate;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<CartProduct> cartProductList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

}