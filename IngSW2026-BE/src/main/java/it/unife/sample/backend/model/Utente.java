package it.unife.sample.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UTENTE")
public class Utente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NOME", nullable = false)
	private String nome;

	@Column(name = "COGNOME", nullable = false)
	private String cognome;

	@Column(name = "NUMERO_TELEFONO")
	private String numeroTelefono;

	@Column(name = "RUOLO", nullable = false)
	private String ruolo;

	@Column(name = "EMAIL", nullable = false)
	private String email;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "CITTA")
	private String citta;

	@Column(name = "CAP")
	private String cap;

	@Column(name = "VIA")
	private String via;

	@Column(name = "NUMERO_CIVICO")
	private String numeroCivico;
}
