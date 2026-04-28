package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.LoginRequestDTO;
import it.unife.sample.backend.dto.RegisterRequestDTO;
import it.unife.sample.backend.dto.UtenteDTO;
import it.unife.sample.backend.service.UtenteService;
import jakarta.servlet.http.HttpSession;
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

	@PostMapping({"/auth/login", "/api/auth/login"})
	public ResponseEntity<UtenteDTO> login(@RequestBody LoginRequestDTO request, HttpSession session) {
		try {
			// Qui teniamo il controller minimale: delega tutta la logica al service.
			UtenteDTO loggedUser = utenteService.login(request);
			session.setAttribute("loggedUserId", loggedUser.getId());
			return ResponseEntity.ok(loggedUser);
		} catch (IllegalArgumentException exception) {
			// Se email/password non tornano rispondiamo 401.
			// Evitiamo di dare dettagli per non esporre troppo lato utente.
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping({"/auth/register", "/api/auth/register"})
	public ResponseEntity<UtenteDTO> register(@RequestBody RegisterRequestDTO request) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(utenteService.register(request));
		} catch (IllegalArgumentException exception) {
			// Email gia presente: restituiamo errore client senza esporre dettagli sensibili.
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping({"/auth/logout", "/api/auth/logout"})
	public ResponseEntity<Void> logout(HttpSession session) {
		session.removeAttribute("loggedUserId");
		return ResponseEntity.noContent().build();
	}
}
