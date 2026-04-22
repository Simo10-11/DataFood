package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.mapper.CategoriaMapper;
import it.unife.sample.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public List<CategoriaDTO> findAll() {
        // Recupera tutte le categorie e le converte in DTO.
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDTO)
                .toList();
    }
}
