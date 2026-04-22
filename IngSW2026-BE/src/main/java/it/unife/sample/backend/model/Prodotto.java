package it.unife.sample.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "PRODOTTO")
public class Prodotto {

    // ID autoincrement della tabella PRODOTTO.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRIZIONE")
    private String descrizione;

    @Column(name = "PREZZO", nullable = false)
    private BigDecimal prezzo;

    @Column(name = "QUANTITA_DISPONIBILE", nullable = false)
    private Integer quantitaDisponibile;

    @Column(name = "IMMAGINE_URL")
    private String imageUrl;

    // Relazione molti-a-uno: molti prodotti possono appartenere a una categoria.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;
}
