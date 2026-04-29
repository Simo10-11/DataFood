package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.mapper.ProdottoMapper;
import it.unife.sample.backend.model.Categoria;
import it.unife.sample.backend.model.Prodotto;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.CategoriaRepository;
import it.unife.sample.backend.repository.ProdottoRepository;
import it.unife.sample.backend.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;



@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final ProdottoMapper prodottoMapper;
    private final CategoriaRepository categoriaRepository;
    private final UtenteRepository utenteRepository;

    private static final String LOGGED_USER_ID_SESSION_ATTRIBUTE = "loggedUserId";

    public ProdottoService(
            ProdottoRepository prodottoRepository,
            ProdottoMapper prodottoMapper,
            CategoriaRepository categoriaRepository,
            UtenteRepository utenteRepository
    ) {
        this.prodottoRepository = prodottoRepository;
        this.prodottoMapper = prodottoMapper;
        this.categoriaRepository = categoriaRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<ProdottoDTO> findAll() {
        // Recupera tutti i prodotti e li converte in DTO.
        return prodottoRepository.findAll().stream()
                .map(prodottoMapper::toDTO)
                .toList();
    }

    public List<ProdottoDTO> searchByNome(String nome) {
        // Ricerca case-insensitive per nome prodotto.
        return prodottoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(prodottoMapper::toDTO)
                .toList();
    }

    public List<ProdottoDTO> findByCategoriaId(Integer categoriaId) {
        // Filtra i prodotti usando l'id della categoria.
        return prodottoRepository.findByCategoria_Id(categoriaId).stream()
                .map(prodottoMapper::toDTO)
                .toList();
    }

    public ProdottoDTO create(ProdottoDTO request, HttpSession session) {
        requireAdmin(session);
        validateRequest(request);

        Prodotto entity = prodottoMapper.toEntity(request);
        entity.setId(null);
        entity.setCategoria(resolveCategoria(request.getIdCategoria()));

        Prodotto saved = prodottoRepository.save(entity);
        return prodottoMapper.toDTO(saved);
    }

    public ProdottoDTO update(Integer id, ProdottoDTO request, HttpSession session) {
        requireAdmin(session);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Prodotto non trovato");
        }

        validateRequest(request);

        Prodotto existing = prodottoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        existing.setNome(request.getNome().trim());
        existing.setDescrizione(request.getDescrizione());
        existing.setPrezzo(request.getPrezzo());
        existing.setQuantitaDisponibile(request.getQuantitaDisponibile());
        existing.setImageUrl(request.getImageUrl());
        existing.setCategoria(resolveCategoria(request.getIdCategoria()));

        Prodotto saved = prodottoRepository.save(existing);
        return prodottoMapper.toDTO(saved);
    }

    public void delete(Integer id, HttpSession session) {
        requireAdmin(session);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Prodotto non trovato");
        }

        if (!prodottoRepository.existsById(id)) {
            throw new IllegalArgumentException("Prodotto non trovato");
        }

        prodottoRepository.deleteById(id);
    }

    private void validateRequest(ProdottoDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dati prodotto non validi");
        }

        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome prodotto obbligatorio");
        }

        if (request.getPrezzo() == null || request.getPrezzo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Prezzo non valido");
        }

        if (request.getQuantitaDisponibile() == null || request.getQuantitaDisponibile() < 0) {
            throw new IllegalArgumentException("Quantita non valida");
        }

        if (request.getIdCategoria() == null) {
            throw new IllegalArgumentException("Categoria obbligatoria");
        }
    }

    private Categoria resolveCategoria(Integer categoryId) {
        return categoriaRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria non trovata"));
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
