package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class UtenteDTO {
	private Long id;
	private String email;
	private String nome;
	private String cognome;
	private String ruolo;
	private Integer puntiTotali;
	private Integer puntiDisponibili;
}
