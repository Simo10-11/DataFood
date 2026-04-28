package it.unife.sample.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "WISHLIST")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // Ogni riga wishlist appartiene a un utente.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_UTENTE", nullable = false)
    private Utente utente;

    // Tabella ponte prevista dallo schema SQL esistente.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "WISHLIST_PRODOTTO",
            joinColumns = @JoinColumn(name = "ID_WISHLIST"),
            inverseJoinColumns = @JoinColumn(name = "ID_PRODOTTO")
    )
    private Set<Prodotto> prodotti = new HashSet<>();
}
