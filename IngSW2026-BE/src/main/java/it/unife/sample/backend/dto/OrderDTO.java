package it.unife.sample.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import it.unife.sample.backend.dto.UtenteDTO;

@Data
public class OrderDTO {
    private Long id;
    private String nomeCliente;
    private String data;
    private String status;
    private double totale;
    private List<OrderItemDTO> items = new ArrayList<>();
    private Integer puntiGuadagnati;
    private Integer puntiUtilizzati;
    private Double scontoApplicato;
    private UtenteDTO utenteAggiornato;
}