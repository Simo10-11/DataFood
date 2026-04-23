package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Integer> {
	Optional<Utente> findByEmail(String email);
}
