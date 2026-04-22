package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.CategoriaDTO;

import java.util.List;

public interface CategoriaService {

    List<CategoriaDTO> findAll();
}
