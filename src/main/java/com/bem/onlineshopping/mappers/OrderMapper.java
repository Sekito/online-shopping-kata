package com.bem.onlineshopping.mappers;

import com.bem.onlineshopping.dto.OrderDTO;
import com.bem.onlineshopping.dto.OrderTrackingResponseDTO;
import com.bem.onlineshopping.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "customer.customerId", target = "customerId")
    OrderDTO toDto(Order order);
    Order toEntity(OrderDTO orderDTO);

    List<OrderDTO> toDtoList(List<Order> orders);

    //@Mapping(target = "orderStatus", source = "orderStatus.name")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "totalItems", source = "totalItems")
    @Mapping(target = "deliveryDate", source = "deliveryDate")
    OrderTrackingResponseDTO toTrackingResponseDTO(Order order);
}
