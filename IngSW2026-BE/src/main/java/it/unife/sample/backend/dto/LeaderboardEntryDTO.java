package it.unife.sample.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private int position;
    private String username;
    private Integer puntiTotali;
    private String badge; // "🥇", "🥈", "🥉", ""
}
