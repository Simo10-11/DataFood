package it.unife.sample.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDINE_PRODOTTO")
public class OrdineProdotto {

    @EmbeddedId
    private OrdineProdottoId id = new OrdineProdottoId();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idOrdine")
    @JoinColumn(name = "ID_ORDINE", nullable = false)
    private Ordine ordine;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idProdotto")
    @JoinColumn(name = "ID_PRODOTTO", nullable = false)
    private Prodotto prodotto;

    @Column(name = "QUANTITA", nullable = false)
    private Integer quantita;

    @Column(name = "PREZZO_UNITARIO", nullable = false)
    private BigDecimal prezzoUnitario;
}