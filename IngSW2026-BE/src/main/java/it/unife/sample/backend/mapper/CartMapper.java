package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.CartDTO;
import it.unife.sample.backend.dto.CartItemDTO;
import it.unife.sample.backend.model.Cart;
import it.unife.sample.backend.model.CartItem;

public class CartMapper {

    private CartMapper() {
    }

    public static CartDTO toDTO(Cart cart) {
        CartDTO dto = new CartDTO();

        if (cart == null || cart.getItems() == null) {
            dto.setTotale(0);
            return dto;
        }

        dto.setItems(cart.getItems().stream().map(CartMapper::toDTO).toList());
        dto.setTotale(cart.getTotal());
        return dto;
    }

    public static CartItemDTO toDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(item.getProductId());
        dto.setNome(item.getNome());
        dto.setPrezzo(item.getPrezzo());
        dto.setQuantita(item.getQuantita());
        return dto;
    }
}