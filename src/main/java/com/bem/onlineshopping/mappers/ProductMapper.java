package com.bem.onlineshopping.mappers;

import com.bem.onlineshopping.dto.ProductDTO;
import com.bem.onlineshopping.model.Product;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ProductMapper {
    ProductDTO toDto(Product product);
    Product toEntity(ProductDTO productDTO);
}
