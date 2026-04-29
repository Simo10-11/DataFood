package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.model.Categoria;
import it.unife.sample.backend.model.Prodotto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct genera automaticamente l'implementazione di questa interfaccia 
@Mapper(componentModel = "spring")
public interface ProdottoMapper {

    // Mappa i dati annidati della categoria in campi piatti del DTO.
    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nomeCategoria", source = "categoria.nome")
    ProdottoDTO toDTO(Prodotto entity);

    // Ricostruisce l'associazione categoria partendo solo da idCategoria.
    @Mapping(target = "categoria", source = "idCategoria")
    Prodotto toEntity(ProdottoDTO dto);

    // Metodo di supporto usato da MapStruct per convertire Integer -> Categoria.
    default Categoria map(Integer idCategoria) {
        if (idCategoria == null) {
            return null;
        }
        Categoria categoria = new Categoria();
        categoria.setId(idCategoria);
        return categoria;
    }
}
