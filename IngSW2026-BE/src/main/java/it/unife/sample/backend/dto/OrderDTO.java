package it.unife.sample.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String data;
    private String status;
    private double totale;
    private List<OrderItemDTO> items = new ArrayList<>();
}