package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class WishlistDTO {
    private Long id;
    private Long utenteId;
    private Integer prodottoId;
}
