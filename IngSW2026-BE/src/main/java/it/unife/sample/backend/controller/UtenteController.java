package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.LoginRequestDTO;
import it.unife.sample.backend.dto.RegisterRequestDTO;
import it.unife.sample.backend.dto.UtenteDTO;
import it.unife.sample.backend.service.UtenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtenteController {

	private final UtenteService utenteService;

	public UtenteController(UtenteService utenteService) {
		this.utenteService = utenteService;
	}

	@PostMapping("/auth/login")
	public ResponseEntity<UtenteDTO> login(@RequestBody LoginRequestDTO request) {
		try {
			// Qui teniamo il controller minimale: delega tutta la logica al service.
			return ResponseEntity.ok(utenteService.login(request));
		} catch (IllegalArgumentException exception) {
			// Se email/password non tornano rispondiamo 401.
			// Evitiamo di dare dettagli per non esporre troppo lato utente.
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/auth/register")
	public ResponseEntity<UtenteDTO> register(@RequestBody RegisterRequestDTO request) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(utenteService.register(request));
		} catch (IllegalArgumentException exception) {
			// Email gia presente: restituiamo errore client senza esporre dettagli sensibili.
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
