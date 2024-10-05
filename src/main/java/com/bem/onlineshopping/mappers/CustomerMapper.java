package com.bem.onlineshopping.mappers;

import com.bem.onlineshopping.dto.CustomerDTO;
import com.bem.onlineshopping.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "cart", target = "cart")
    CustomerDTO toDto(Customer customer);

    @Mapping(source = "cart", target = "cart")
    Customer toEntity(CustomerDTO customerDTO);
}
