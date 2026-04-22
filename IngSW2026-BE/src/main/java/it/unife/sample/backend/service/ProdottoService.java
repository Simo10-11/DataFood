package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.ProdottoDTO;

import java.util.List;

public interface ProdottoService {

    List<ProdottoDTO> findAll();

    List<ProdottoDTO> searchByNome(String nome);

    List<ProdottoDTO> findByCategoriaId(Integer categoriaId);
}
