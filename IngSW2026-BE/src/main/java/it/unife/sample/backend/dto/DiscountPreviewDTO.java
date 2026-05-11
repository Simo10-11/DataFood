package it.unife.sample.backend.dto;

import lombok.Data;

@Data
public class DiscountPreviewDTO {
    private double totaleOrdine;
    private int puntiDisponibili;
    private double valorePuntiInEuro;
    private double scontoApplicabile;
    private double totaleConSconto;
    private int puntiUtilizzabili;
}
