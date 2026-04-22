package it.unife.sample.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdottoDTO {
    // Campi esposti al frontend per il catalogo.
    private Integer id;
    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private Integer quantitaDisponibile;
    // Dati categoria in forma piatta per semplificare il consumo lato Angular.
    private Integer idCategoria;
    private String nomeCategoria;
}
