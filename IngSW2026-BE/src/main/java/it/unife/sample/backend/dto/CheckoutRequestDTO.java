package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class CheckoutRequestDTO {
    private boolean usePunti;
    private Integer puntiDaUtilizzare;
}
