package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer productId;
    private String nome;
    private double prezzo;
    private Integer quantita;
}