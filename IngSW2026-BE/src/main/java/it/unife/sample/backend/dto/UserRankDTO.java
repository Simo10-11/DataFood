package it.unife.sample.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRankDTO {
    private Integer rank; // Posizione in classifica
    private String username;
    private Integer puntiTotali;
}
