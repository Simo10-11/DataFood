package it.unife.sample.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrdineProdottoId implements Serializable {

    @Column(name = "ID_PRODOTTO")
    private Integer idProdotto;

    @Column(name = "ID_ORDINE")
    private Long idOrdine;
}