package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.LeaderboardDTO;
import it.unife.sample.backend.dto.LeaderboardEntryDTO;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final UtenteRepository utenteRepository;

    public LeaderboardService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    /**
     * Ottiene la leaderboard ordinata per punti totali
     * Mostra solo utenti con punti > 0, top 10
     */
    public LeaderboardDTO getLeaderboard() {
        List<Utente> topUtenti = utenteRepository.findAll()
            .stream()
            .filter(u -> u.getPuntiTotali() != null && u.getPuntiTotali() > 0)
            .sorted((u1, u2) -> u2.getPuntiTotali().compareTo(u1.getPuntiTotali()))
            .limit(10)
            .collect(Collectors.toList());

        List<LeaderboardEntryDTO> entries = topUtenti.stream()
            .map(this::convertToLeaderboardEntry)
            .collect(Collectors.toList());

        return new LeaderboardDTO(entries);
    }

    /**
     * Ottiene la posizione dell'utente nella leaderboard
     * Basata su punti totali
     */
    public int getUserRank(Long utenteId) {
        Optional<Utente> utente = utenteRepository.findById(utenteId);
        if (utente.isEmpty() || utente.get().getPuntiTotali() == null || utente.get().getPuntiTotali() <= 0) {
            return -1;
        }

        List<Utente> allUsers = utenteRepository.findAll();
        List<Utente> ranked = allUsers.stream()
            .filter(u -> u.getPuntiTotali() != null && u.getPuntiTotali() > 0)
            .sorted((u1, u2) -> u2.getPuntiTotali().compareTo(u1.getPuntiTotali()))
            .collect(Collectors.toList());

        for (int i = 0; i < ranked.size(); i++) {
            if (ranked.get(i).getId().equals(utenteId)) {
                return i + 1; // Posizione 1-based
            }
        }

        return -1;
    }

    /**
     * Aggiorna i punti giornalieri dell'utente (chiamato all'ordine)
     */
    @Transactional
    public void updatePuntiGiornalieri(Utente utente, int puntiGuadagnati) {
        if (puntiGuadagnati > 0) {
            utente.setPuntiGiornalieri(utente.getPuntiGiornalieri() + puntiGuadagnati);
            utenteRepository.save(utente);
        }
    }

    /**
     * Resetta tutti i punti giornalieri (scheduler - ogni giorno)
     * IMPORTANTE: cancella solo PUNTI_GIORNALIERI, NON puntiTotali e puntiDisponibili
     */
    @Transactional
    public void resetLeaderboardDaily() {
        List<Utente> allUsers = utenteRepository.findAll();
        for (Utente utente : allUsers) {
            utente.setPuntiGiornalieri(0);
        }
        utenteRepository.saveAll(allUsers);
    }

    /**
     * Converte Utente a LeaderboardEntryDTO con ranking e badge
     * Basato su punti totali
     */
    private LeaderboardEntryDTO convertToLeaderboardEntry(Utente utente) {
        List<Utente> allUsers = utenteRepository.findAll();
        List<Utente> ranked = allUsers.stream()
            .filter(u -> u.getPuntiTotali() != null && u.getPuntiTotali() > 0)
            .sorted((u1, u2) -> u2.getPuntiTotali().compareTo(u1.getPuntiTotali()))
            .collect(Collectors.toList());

        int position = 1;
        for (int i = 0; i < ranked.size(); i++) {
            if (ranked.get(i).getId().equals(utente.getId())) {
                position = i + 1;
                break;
            }
        }

        String badge = getBadge(position);

        return new LeaderboardEntryDTO(
            position,
            utente.getNome() + " " + utente.getCognome(),
            utente.getPuntiTotali(),
            badge
        );
    }

    /**
     * Restituisce il badge per le top 3 posizioni
     */
    private String getBadge(int position) {
        return switch (position) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "";
        };
    }
}
