package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.mapper.ProdottoMapper;
import it.unife.sample.backend.repository.ProdottoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdottoServiceImpl implements ProdottoService {

    private final ProdottoRepository prodottoRepository;

    public ProdottoServiceImpl(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

    @Override
    public List<ProdottoDTO> findAll() {
        // Recupera tutti i prodotti e li converte in DTO.
        return prodottoRepository.findAll().stream()
                .map(ProdottoMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProdottoDTO> searchByNome(String nome) {
        // Ricerca case-insensitive per nome prodotto.
        return prodottoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(ProdottoMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProdottoDTO> findByCategoriaId(Integer categoriaId) {
        // Filtra i prodotti usando l'id della categoria.
        return prodottoRepository.findByCategoria_Id(categoriaId).stream()
                .map(ProdottoMapper::toDTO)
                .toList();
    }
}
