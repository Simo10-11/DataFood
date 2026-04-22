package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.service.ProdottoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/products", "/products"})
public class ProdottoController {

    private final ProdottoService prodottoService;

    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdottoDTO>> getAllProducts() {
        // Restituisce il catalogo completo prodotti.
        return ResponseEntity.ok(prodottoService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProdottoDTO>> searchProducts(@RequestParam(defaultValue = "") String nome) {
        // Ricerca prodotti per nome: /products/search?nome=...
        return ResponseEntity.ok(prodottoService.searchByNome(nome));
    }

    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<ProdottoDTO>> getByCategoria(@PathVariable Integer id) {
        // Filtra prodotti per categoria: /products/categoria/{id}
        return ResponseEntity.ok(prodottoService.findByCategoriaId(id));
    }
}
