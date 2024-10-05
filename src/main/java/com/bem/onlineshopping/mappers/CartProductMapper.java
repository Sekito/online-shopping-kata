package com.bem.onlineshopping.mappers;

import com.bem.onlineshopping.dto.CartProductDTO;
import com.bem.onlineshopping.model.CartProduct;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartProductMapper {
    CartProductMapper INSTANCE = Mappers.getMapper(CartProductMapper.class);

    //@Mapping(source = "cart", target = "cart")
    CartProductDTO toDto(CartProduct cartProduct);

    //@Mapping(source = "cart", target = "cart")
    CartProduct toEntity(CartProductDTO cartProductDTO);
}
