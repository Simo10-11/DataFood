package it.unife.sample.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import it.unife.sample.backend.repository.UtenteRepository;
import it.unife.sample.backend.model.Utente;

@Configuration
public class PasswordMigrationConfig {

    @Bean
    public CommandLineRunner migratePasswordsToHash(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica se ci sono utenti con password che non iniziano con $ (ovvero non sono hash BCrypt)
            var allUtenti = utenteRepository.findAll();
            boolean needsMigration = allUtenti.stream()
                    .anyMatch(u -> !u.getPassword().startsWith("$2a$") && !u.getPassword().startsWith("$2b$"));

            if (needsMigration) {
                System.out.println("=== Migrazione password in corso ===");
                for (Utente utente : allUtenti) {
                    // Se la password non è già hashata (non inizia con $ di BCrypt), la hashiamo
                    if (!utente.getPassword().startsWith("$2a$") && !utente.getPassword().startsWith("$2b$")) {
                        String hashedPassword = passwordEncoder.encode(utente.getPassword());
                        utente.setPassword(hashedPassword);
                        utenteRepository.save(utente);
                        System.out.println("✓ Password di " + utente.getEmail() + " convertita a hash");
                    }
                }
                System.out.println("=== Migrazione completata ===");
            }
        };
    }
}
