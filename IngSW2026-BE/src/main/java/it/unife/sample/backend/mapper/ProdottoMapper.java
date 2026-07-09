package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.model.Categoria;
import it.unife.sample.backend.model.Prodotto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Mappa i prodotti tra entity e DTO con categoria appiattita
@Mapper(componentModel = "spring")
public interface ProdottoMapper {

    // Porta i dati della categoria dentro il DTO prodotto
    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nomeCategoria", source = "categoria.nome")
    ProdottoDTO toDTO(Prodotto entity);

    // Ricostruisce il collegamento alla categoria a partire dall id
    @Mapping(target = "categoria", source = "idCategoria")
    Prodotto toEntity(ProdottoDTO dto);

    // Supporto MapStruct per convertire un id categoria in entity
    default Categoria map(Integer idCategoria) {
        if (idCategoria == null) {
            return null;
        }
        Categoria categoria = new Categoria();
        categoria.setId(idCategoria);
        return categoria;
    }
}
