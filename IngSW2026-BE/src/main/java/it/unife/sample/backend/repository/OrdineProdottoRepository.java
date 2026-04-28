package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.OrdineProdotto;
import it.unife.sample.backend.model.OrdineProdottoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdineProdottoRepository extends JpaRepository<OrdineProdotto, OrdineProdottoId> {
}