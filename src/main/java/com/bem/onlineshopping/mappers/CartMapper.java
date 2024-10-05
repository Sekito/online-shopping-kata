package com.bem.onlineshopping.mappers;

import com.bem.onlineshopping.dto.CartDTO;
import com.bem.onlineshopping.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CartProductMapper.class)
public interface CartMapper {
    CartDTO toDto(Cart cart);
    Cart toEntity(CartDTO cartDTO);
}
