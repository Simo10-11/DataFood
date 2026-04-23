package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.LoginRequestDTO;
import it.unife.sample.backend.dto.UtenteDTO;
import it.unife.sample.backend.mapper.UtenteMapper;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.UtenteRepository;
import org.springframework.stereotype.Service;

@Service
public class UtenteService {

	private final UtenteRepository utenteRepository;
	private final UtenteMapper utenteMapper;

	public UtenteService(UtenteRepository utenteRepository, UtenteMapper utenteMapper) {
		this.utenteRepository = utenteRepository;
		this.utenteMapper = utenteMapper;
	}

	public UtenteDTO login(LoginRequestDTO request) {
		// Primo step: cerchiamo per email, cosi gestiamo il caso "utente inesistente" subito.
		Utente utente = utenteRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

		// Password in chiaro solo per il progetto didattico.
		// In una versione reale qui andrebbe hash + confronto sicuro.
		if (!utente.getPassword().equals(request.getPassword())) {
			throw new IllegalArgumentException("Password errata");
		}

		// Torniamo un DTO per non esporre campi sensibili (es. password).
		return utenteMapper.toDTO(utente);
	}
}

