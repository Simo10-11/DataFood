package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.model.Categoria;
import it.unife.sample.backend.model.Prodotto;

public final class ProdottoMapper {

    private ProdottoMapper() {
    }

    public static ProdottoDTO toDTO(Prodotto entity) {
        if (entity == null) {
            return null;
        }

        // Converte l'entita del database in DTO da esporre via API.
        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setDescrizione(entity.getDescrizione());
        dto.setPrezzo(entity.getPrezzo());
        dto.setQuantitaDisponibile(entity.getQuantitaDisponibile());

        // Copia i dati minimi della categoria per il frontend.
        if (entity.getCategoria() != null) {
            dto.setIdCategoria(entity.getCategoria().getId());
            dto.setNomeCategoria(entity.getCategoria().getNome());
        }

        return dto;
    }

    public static Prodotto toEntity(ProdottoDTO dto) {
        if (dto == null) {
            return null;
        }

        // Converte il DTO ricevuto in input in entita JPA.
        Prodotto entity = new Prodotto();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setDescrizione(dto.getDescrizione());
        entity.setPrezzo(dto.getPrezzo());
        entity.setQuantitaDisponibile(dto.getQuantitaDisponibile());

        // Associa la categoria tramite id, senza caricare tutto l'oggetto.
        if (dto.getIdCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getIdCategoria());
            entity.setCategoria(categoria);
        }

        return entity;
    }
}
