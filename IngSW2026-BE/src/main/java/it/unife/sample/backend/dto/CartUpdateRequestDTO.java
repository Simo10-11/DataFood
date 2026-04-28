package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class CartUpdateRequestDTO {
    private Long productId;
    private Integer quantita;
}