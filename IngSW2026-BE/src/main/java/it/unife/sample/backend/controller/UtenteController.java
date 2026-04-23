package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.LoginRequestDTO;
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
			return ResponseEntity.ok(utenteService.login(request));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
