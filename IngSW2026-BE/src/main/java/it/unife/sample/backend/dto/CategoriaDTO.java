package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class CategoriaDTO {
    // DTO semplice usato per popolare la sidebar categorie.
    private Integer id;
    private String nome;
    private String descrizione;
}
