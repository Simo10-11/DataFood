package it.unife.sample.backend.service;

import it.unife.sample.backend.model.Ordine;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PuntiService {

    private static final int PUNTI_PER_EURO = 20;
    private static final int VALORE_PUNTO_EURO = 1;

    private final UtenteRepository utenteRepository;
    private final LeaderboardService leaderboardService;

    public PuntiService(UtenteRepository utenteRepository, LeaderboardService leaderboardService) {
        this.utenteRepository = utenteRepository;
        this.leaderboardService = leaderboardService;
    }

    /**
     * Calcola i punti guadagnati per un ordine
     * 1 punto ogni 20€ spesi (per difetto)
     */
    public int calculatePuntiGuadagnati(BigDecimal importoOrdine) {
        if (importoOrdine == null || importoOrdine.signum() <= 0) {
            return 0;
        }
        return importoOrdine.divide(BigDecimal.valueOf(PUNTI_PER_EURO)).intValue();
    }

    /**
     * Aggiorna i punti dopo la conferma ordine
     * - Incrementa sia puntiTotali che puntiDisponibili
     * - Incrementa puntiGiornalieri (per la leaderboard)
     * - Salva nel database
     */
    @Transactional
    public void aggiungiPunti(Utente utente, int puntiGuadagnati) {
        if (puntiGuadagnati <= 0) {
            return;
        }

        utente.setPuntiTotali(utente.getPuntiTotali() + puntiGuadagnati);
        utente.setPuntiDisponibili(utente.getPuntiDisponibili() + puntiGuadagnati);
        utente.setPuntiGiornalieri(utente.getPuntiGiornalieri() + puntiGuadagnati);

        utenteRepository.save(utente);
    }

    /**
     * Converte punti disponibili in euro
     * 1 punto = 1€
     */
    public double convertPuntiToEuro(int punti) {
        return punti * VALORE_PUNTO_EURO;
    }

    /**
     * Applica sconto utilizzando punti
     * Parametri:
     * - puntiDisponibili: punti che l'utente vuole utilizzare
     * - totaleOrdine: totale dell'ordine in euro
     *
     * Restituisce:
     * - sconto in euro (massimo il totale dell'ordine)
     * - numero di punti effettivamente utilizzati
     */
    public DiscountResult applicaScontoWithPunti(int puntiDisponibili, BigDecimal totaleOrdine) {
        if (puntiDisponibili <= 0 || totaleOrdine == null || totaleOrdine.signum() <= 0) {
            return new DiscountResult(0, 0.0, BigDecimal.ZERO);
        }

        // Converte punti in euro
        double scontoMassimo = convertPuntiToEuro(puntiDisponibili);

        // Il sconto non può superare il totale dell'ordine
        double scontoEffettivo = Math.min(scontoMassimo, totaleOrdine.doubleValue());

        // Calcola quanti punti sono stati effettivamente utilizzati
        int puntiUtilizzati = (int) scontoEffettivo;

        // Nuovo totale
        BigDecimal nuovoTotale = totaleOrdine.subtract(BigDecimal.valueOf(scontoEffettivo));

        return new DiscountResult(puntiUtilizzati, scontoEffettivo, nuovoTotale);
    }

    /**
     * Decrementa i punti disponibili dopo l'uso
     * I punti totali NON diminuiscono mai
     */
    @Transactional
    public void usaPunti(Utente utente, int puntiUsati) {
        if (puntiUsati <= 0) {
            return;
        }

        int nuoviPuntiDisponibili = Math.max(0, utente.getPuntiDisponibili() - puntiUsati);
        utente.setPuntiDisponibili(nuoviPuntiDisponibili);

        utenteRepository.save(utente);
    }

    /**
     * Risultato dell'applicazione dello sconto con punti
     */
    public static class DiscountResult {
        public final int puntiUtilizzati;
        public final double scontoApplicato;
        public final BigDecimal nuovoTotale;

        public DiscountResult(int puntiUtilizzati, double scontoApplicato, BigDecimal nuovoTotale) {
            this.puntiUtilizzati = puntiUtilizzati;
            this.scontoApplicato = scontoApplicato;
            this.nuovoTotale = nuovoTotale;
        }
    }
}
