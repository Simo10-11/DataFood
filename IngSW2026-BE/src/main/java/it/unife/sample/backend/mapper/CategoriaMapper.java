package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.model.Categoria;
import org.mapstruct.Mapper;

// Mapper dichiarativo: MapStruct genera la classe concreta e Spring la inietta come bean.
@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    // Entity -> DTO
    CategoriaDTO toDTO(Categoria entity);

    // DTO -> Entity
    Categoria toEntity(CategoriaDTO dto);
}
