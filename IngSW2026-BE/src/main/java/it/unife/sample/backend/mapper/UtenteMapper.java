package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.UtenteDTO;
import it.unife.sample.backend.model.Utente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

	UtenteDTO toDTO(Utente entity);

	@Mapping(target = "numeroTelefono", ignore = true)
	@Mapping(target = "ruolo", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "citta", ignore = true)
	@Mapping(target = "cap", ignore = true)
	@Mapping(target = "via", ignore = true)
	@Mapping(target = "numeroCivico", ignore = true)
	Utente toEntity(UtenteDTO dto);
}
