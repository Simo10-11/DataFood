package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long productId;
    private String nome;
    private double prezzo;
    private int quantita;
}