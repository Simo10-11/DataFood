package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.mapper.ProdottoMapper;
import it.unife.sample.backend.repository.ProdottoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final ProdottoMapper prodottoMapper;

    public ProdottoService(ProdottoRepository prodottoRepository, ProdottoMapper prodottoMapper) {
        this.prodottoRepository = prodottoRepository;
        this.prodottoMapper = prodottoMapper;
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
}
