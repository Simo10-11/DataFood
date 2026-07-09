package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.ProdottoDTO;
import it.unife.sample.backend.service.ProdottoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // Restituisce tutti i prodotti del catalogo
    @GetMapping
    public ResponseEntity<List<ProdottoDTO>> getAllProducts() {
        return ResponseEntity.ok(prodottoService.findAll());
    }

    // Cerca i prodotti per nome
    @GetMapping("/search")
    public ResponseEntity<List<ProdottoDTO>> searchProducts(@RequestParam(defaultValue = "") String nome) {
        return ResponseEntity.ok(prodottoService.searchByNome(nome));
    }

    // Filtra i prodotti in base alla categoria
    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<ProdottoDTO>> getByCategoria(@PathVariable Integer id) {
        return ResponseEntity.ok(prodottoService.findByCategoriaId(id));
    }

    // Crea un nuovo prodotto nel catalogo
    @PostMapping
    public ResponseEntity<ProdottoDTO> createProduct(@RequestBody ProdottoDTO request, HttpSession session) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(prodottoService.create(request, session));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException exception) {
            if (isNotFoundError(exception)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Aggiorna i dati di un prodotto esistente
    @PutMapping("/{id}")
    public ResponseEntity<ProdottoDTO> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProdottoDTO request,
            HttpSession session
    ) {
        try {
            return ResponseEntity.ok(prodottoService.update(id, request, session));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException exception) {
            if (isNotFoundError(exception)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Elimina un prodotto dal catalogo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id, HttpSession session) {
        try {
            prodottoService.delete(id, session);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException exception) {
            if (isNotFoundError(exception)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isNotFoundError(IllegalArgumentException exception) {
        String message = exception.getMessage();
        return message != null && message.toLowerCase().contains("non trovato");
    }
}
