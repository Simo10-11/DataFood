package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.service.CategoriaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/categorie", "/categorie"})
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaDTO> getAllCategorie() {
        // Restituisce l'elenco completo delle categorie disponibili.
        return categoriaService.findAll();
    }
}
