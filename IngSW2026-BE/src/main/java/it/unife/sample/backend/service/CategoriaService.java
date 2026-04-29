package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.mapper.CategoriaMapper;
import it.unife.sample.backend.model.Categoria;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.CategoriaRepository;
import it.unife.sample.backend.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final UtenteRepository utenteRepository;

    private static final String LOGGED_USER_ID_SESSION_ATTRIBUTE = "loggedUserId";

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            CategoriaMapper categoriaMapper,
            UtenteRepository utenteRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
        this.utenteRepository = utenteRepository;
    }

    public List<CategoriaDTO> findAll() {
        // Recupera tutte le categorie e le converte in DTO.
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDTO)
                .toList();
    }

    public CategoriaDTO create(CategoriaDTO request, HttpSession session) {
        requireAdmin(session);
        validateRequest(request);

        String normalizedName = request.getNome().trim();
        if (categoriaRepository.existsByNomeIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Nome categoria gia esistente");
        }

        Categoria entity = new Categoria();
        entity.setNome(normalizedName);
        entity.setDescrizione(request.getDescrizione());

        Categoria saved = categoriaRepository.save(entity);
        return categoriaMapper.toDTO(saved);
    }

    public CategoriaDTO update(Integer id, CategoriaDTO request, HttpSession session) {
        requireAdmin(session);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Categoria non trovata");
        }

        validateRequest(request);

        Categoria existing = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria non trovata"));

        String normalizedName = request.getNome().trim();
        categoriaRepository.findByNomeIgnoreCase(normalizedName)
                .ifPresent(categoryByName -> {
                    if (!categoryByName.getId().equals(existing.getId())) {
                        throw new IllegalArgumentException("Nome categoria gia esistente");
                    }
                });

        existing.setNome(normalizedName);
        existing.setDescrizione(request.getDescrizione());

        Categoria saved = categoriaRepository.save(existing);
        return categoriaMapper.toDTO(saved);
    }

    public void delete(Integer id, HttpSession session) {
        requireAdmin(session);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Categoria non trovata");
        }

        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoria non trovata");
        }

        try {
            categoriaRepository.deleteById(id);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalStateException("Categoria collegata a prodotti");
        }
    }

    private void validateRequest(CategoriaDTO request) {
        if (request == null || request.getNome() == null || request.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome categoria obbligatorio");
        }
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
