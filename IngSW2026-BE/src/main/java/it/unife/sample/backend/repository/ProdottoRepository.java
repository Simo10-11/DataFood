package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Integer> {

    List<Prodotto> findByNomeContainingIgnoreCase(String nome);

    List<Prodotto> findByCategoria_Id(Integer categoriaId);
}
