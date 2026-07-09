package it.unife.sample.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// Usiamo Lombok per generare automaticamente getter, setter e altri metodi di base e tenere il model piu pulito
@Data
@Entity
@Table(name = "CATEGORIA")
public class Categoria {

    // ID autoincrement della tabella CATEGORIA.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRIZIONE")
    private String descrizione;
}
