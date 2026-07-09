package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.CategoriaDTO;
import it.unife.sample.backend.service.CategoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // Restituisce tutte le categorie disponibili
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> getAllCategorie() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    // Crea una nuova categoria se l'utente ha i permessi
    @PostMapping
    public ResponseEntity<CategoriaDTO> createCategory(@RequestBody CategoriaDTO request, HttpSession session) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.create(request, session));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Aggiorna i dati di una categoria esistente
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> updateCategory(
            @PathVariable Integer id,
            @RequestBody CategoriaDTO request,
            HttpSession session
    ) {
        try {
            return ResponseEntity.ok(categoriaService.update(id, request, session));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException exception) {
            if (isNotFoundError(exception)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Elimina una categoria se non e collegata a prodotti
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id, HttpSession session) {
        try {
            categoriaService.delete(id, session);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException exception) {
            if (isLinkedCategoryError(exception)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
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
        return message != null && message.toLowerCase().contains("non trovata");
    }

    private boolean isLinkedCategoryError(IllegalStateException exception) {
        String message = exception.getMessage();
        return message != null && message.toLowerCase().contains("collegata");
    }
}
