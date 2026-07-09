package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.UtenteDTO;
import it.unife.sample.backend.model.Utente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

	// Converte un utente in DTO per l API
	UtenteDTO toDTO(Utente entity);

	// Converte un DTO utente in entity ignorando i campi gestiti dal backend
	@Mapping(target = "numeroTelefono", ignore = true)
	@Mapping(target = "ruolo", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "citta", ignore = true)
	@Mapping(target = "cap", ignore = true)
	@Mapping(target = "via", ignore = true)
	@Mapping(target = "numeroCivico", ignore = true)
	@Mapping(target = "puntiDisponibili", ignore = true)
	Utente toEntity(UtenteDTO dto);
}
