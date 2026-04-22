package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.model.Categoria;
import it.unife.sample.backend.model.Prodotto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdottoMapper {

    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nomeCategoria", source = "categoria.nome")
    ProdottoDTO toDTO(Prodotto entity);

    @Mapping(target = "categoria", source = "idCategoria")
    Prodotto toEntity(ProdottoDTO dto);

    default Categoria map(Integer idCategoria) {
        if (idCategoria == null) {
            return null;
        }
        Categoria categoria = new Categoria();
        categoria.setId(idCategoria);
        return categoria;
    }
}
