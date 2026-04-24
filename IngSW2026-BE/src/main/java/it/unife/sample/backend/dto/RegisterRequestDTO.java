package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
	private String email;
	private String password;
	private String nome;
	private String cognome;
}