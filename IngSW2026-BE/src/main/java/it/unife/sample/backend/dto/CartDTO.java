package it.unife.sample.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private double totale;
}