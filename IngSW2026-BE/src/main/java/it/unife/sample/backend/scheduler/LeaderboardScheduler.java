package it.unife.sample.backend.scheduler;

import it.unife.sample.backend.service.LeaderboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler per il reset giornaliero della leaderboard
 * Resetta i punti giornalieri di tutti gli utenti ogni giorno a mezzanotte
 */
@Slf4j
@Component
public class LeaderboardScheduler {

    private final LeaderboardService leaderboardService;

    public LeaderboardScheduler(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /**
     * Esegue il reset della leaderboard ogni giorno a mezzanotte (00:00)
     * Timezone: Europe/Rome (UTC+1 o UTC+2)
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Rome")
    public void resetLeaderboardDaily() {
        try {
            log.info("🔄 Reset leaderboard - Azzeramento punti giornalieri di tutti gli utenti");
            leaderboardService.resetLeaderboardDaily();
            log.info("✅ Reset completato - Leaderboard ripristinata");
        } catch (Exception e) {
            log.error("❌ Errore durante il reset della leaderboard: {}", e.getMessage(), e);
        }
    }
}
