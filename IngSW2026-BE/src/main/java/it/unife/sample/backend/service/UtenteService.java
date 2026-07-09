package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.LoginRequestDTO;
import it.unife.sample.backend.dto.RegisterRequestDTO;
import it.unife.sample.backend.dto.UtenteDTO;
import it.unife.sample.backend.mapper.UtenteMapper;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.OrdineRepository;
import it.unife.sample.backend.repository.UtenteRepository;
import it.unife.sample.backend.repository.WishlistRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtenteService {

	private final UtenteRepository utenteRepository;
	private final UtenteMapper utenteMapper;
	private final OrdineRepository ordineRepository;
	private final WishlistRepository wishlistRepository;
	private final PasswordEncoder passwordEncoder;	//singlenton per default
	private static final String LOGGED_USER_ID_SESSION_ATTRIBUTE = "loggedUserId";

	public UtenteService(
			UtenteRepository utenteRepository,
			UtenteMapper utenteMapper,
			OrdineRepository ordineRepository,
			WishlistRepository wishlistRepository,
			PasswordEncoder passwordEncoder
	) {
		this.utenteRepository = utenteRepository;
		this.utenteMapper = utenteMapper;
		this.ordineRepository = ordineRepository;
		this.wishlistRepository = wishlistRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// Autentica un utente verificando email e password hashata
	public UtenteDTO login(LoginRequestDTO request) {
		Utente utente = utenteRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

		if (!passwordEncoder.matches(request.getPassword(), utente.getPassword())) {
			throw new IllegalArgumentException("Password errata");
		}

		return utenteMapper.toDTO(utente);
	}

	// Registra un nuovo utente con password hashata
	public UtenteDTO register(RegisterRequestDTO request) {
		if (utenteRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email gia registrata");
		}

		String hashedPassword = passwordEncoder.encode(request.getPassword());

		Utente nuovoUtente = new Utente(
				null,
				request.getNome(),
				request.getCognome(),
				null,
				"cliente",
				request.getEmail(),
				hashedPassword,
				null,
				null,
				null,
				null,
				0
		);

		Utente salvato = utenteRepository.save(nuovoUtente);
		return utenteMapper.toDTO(salvato);
	}

	// Ripristina la sessione backend a partire dall id utente
	public UtenteDTO restoreSession(Long userId, jakarta.servlet.http.HttpSession session) {
		if (userId == null) {
			throw new IllegalArgumentException("Utente non trovato");
		}

		Utente utente = utenteRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

		session.setAttribute("loggedUserId", utente.getId());
		session.setAttribute("loggedUserRole", utente.getRuolo());

		return utenteMapper.toDTO(utente);
	}

	// Restituisce tutti gli utenti se chi ha la sessione è admin
	public List<UtenteDTO> findAllUsers(HttpSession session) {
		requireAdmin(session);
		return utenteRepository.findAll().stream()
				.map(utenteMapper::toDTO)
				.toList();
	}

	// Elimina un utente e pulisce i dati collegati
	@Transactional
	public void deleteUser(Long userId, HttpSession session) {
		requireAdmin(session);

		if (userId == null || userId <= 0) {
			throw new IllegalArgumentException("Utente non trovato");
		}

		Utente currentUser = getLoggedUser(session);
		if (currentUser.getId().equals(userId)) {
			throw new IllegalStateException("Non puoi eliminare il tuo account");
		}

		Utente target = utenteRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

		if (target.getRuolo() != null && "admin".equalsIgnoreCase(target.getRuolo())) {
			throw new IllegalStateException("Non puoi eliminare un amministratore");
		}

		ordineRepository.deleteByUtenteId(userId);
		wishlistRepository.deleteByUtente_Id(userId);
		utenteRepository.deleteById(userId);
	}

	private void requireAdmin(HttpSession session) {
		Utente utente = getLoggedUser(session);
		if (utente.getRuolo() == null || !"admin".equalsIgnoreCase(utente.getRuolo())) {
			throw new IllegalStateException("Utente non autorizzato");
		}
	}

	private Utente getLoggedUser(HttpSession session) {
		Object userIdObj = session.getAttribute(LOGGED_USER_ID_SESSION_ATTRIBUTE);
		if (userIdObj == null) {
			throw new IllegalStateException("Utente non autenticato");
		}

		Long userId;
		if (userIdObj instanceof Long longValue) {
			userId = longValue;
		} else if (userIdObj instanceof Integer intValue) {
			userId = intValue.longValue();
		} else {
			throw new IllegalStateException("Utente non autenticato");
		}

		return utenteRepository.findById(userId)
				.orElseThrow(() -> new IllegalStateException("Utente non autenticato"));
	}
}

