package it.unife.sample.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
// Rappresenta il carrello per il trasferimento dati verso il client
public class CartDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private double totale;
}