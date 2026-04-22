package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.mapper.CategoriaMapper;
import it.unife.sample.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<CategoriaDTO> findAll() {
        // Recupera tutte le categorie e le converte in DTO.
        return categoriaRepository.findAll().stream()
                .map(CategoriaMapper::toDTO)
                .toList();
    }
}
