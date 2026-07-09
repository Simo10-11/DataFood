package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.model.Categoria;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    // Converte una categoria in DTO
    CategoriaDTO toDTO(Categoria entity);

    // Converte un DTO categoria in entity
    Categoria toEntity(CategoriaDTO dto);
}
