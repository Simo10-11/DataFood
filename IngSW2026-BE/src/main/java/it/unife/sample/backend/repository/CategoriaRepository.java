package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

	boolean existsByNomeIgnoreCase(String nome);

	Optional<Categoria> findByNomeIgnoreCase(String nome);
}
