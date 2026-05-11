package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.LeaderboardDTO;
import it.unife.sample.backend.dto.UserRankDTO;
import it.unife.sample.backend.service.LeaderboardService;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.UtenteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final UtenteRepository utenteRepository;

    public LeaderboardController(LeaderboardService leaderboardService, UtenteRepository utenteRepository) {
        this.leaderboardService = leaderboardService;
        this.utenteRepository = utenteRepository;
    }

    /**
     * GET /api/leaderboard
     * Restituisce la leaderboard giornaliera (top 100)
     */
    @GetMapping
    public ResponseEntity<LeaderboardDTO> getLeaderboard() {
        LeaderboardDTO leaderboard = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    /**
     * GET /api/leaderboard/rank/{utenteId}
     * Restituisce la posizione dell'utente nella leaderboard
     */
    @GetMapping("/rank/{utenteId}")
    public ResponseEntity<UserRankDTO> getUserRank(@PathVariable Long utenteId) {
        Optional<Utente> utente = utenteRepository.findById(utenteId);
        if (utente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        int rank = leaderboardService.getUserRank(utenteId);
        Utente u = utente.get();

        UserRankDTO userRank = new UserRankDTO(
            rank,
            u.getNome() + " " + u.getCognome(),
            u.getPuntiTotali()
        );

        return ResponseEntity.ok(userRank);
    }
}
