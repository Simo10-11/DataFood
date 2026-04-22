package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.model.Categoria;

public final class CategoriaMapper {

    private CategoriaMapper() {
    }

    public static CategoriaDTO toDTO(Categoria entity) {
        if (entity == null) {
            return null;
        }

        // Converte l'entita database in DTO per le API.
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setDescrizione(entity.getDescrizione());
        return dto;
    }

    public static Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) {
            return null;
        }

        // Converte il DTO ricevuto dal client in entita JPA.
        Categoria entity = new Categoria();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setDescrizione(dto.getDescrizione());
        return entity;
    }
}
