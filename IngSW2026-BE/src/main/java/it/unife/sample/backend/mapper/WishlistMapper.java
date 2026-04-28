package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.WishlistDTO;
import it.unife.sample.backend.model.Prodotto;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.model.Wishlist;

import java.util.HashSet;
import java.util.Set;

public class WishlistMapper {

    private WishlistMapper() {
        // Utility class: non istanziabile.
    }

    public static WishlistDTO toDTO(Wishlist entity) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(entity.getId());
        dto.setUtenteId(entity.getUtente() != null ? entity.getUtente().getId() : null);
        dto.setProdottoId(entity.getProdotti().stream()
                .findFirst()
                .map(Prodotto::getId)
                .orElse(null));
        return dto;
    }

    public static Wishlist toEntity(WishlistDTO dto, Utente utente, Prodotto prodotto) {
        Wishlist entity = new Wishlist();
        entity.setId(dto.getId());
        entity.setUtente(utente);

        Set<Prodotto> prodotti = new HashSet<>();
        if (prodotto != null) {
            prodotti.add(prodotto);
        }
        entity.setProdotti(prodotti);

        return entity;
    }
}
