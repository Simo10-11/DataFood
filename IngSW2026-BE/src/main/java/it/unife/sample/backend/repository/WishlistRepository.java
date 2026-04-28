package it.unife.sample.backend.repository;

import it.unife.sample.backend.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUtente_Id(Long utenteId);

    @Query("""
            SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END
            FROM Wishlist w
            JOIN w.prodotti p
            WHERE w.utente.id = :utenteId
            AND p.id = :prodottoId
            """)
    boolean existsByUtenteIdAndProdottoId(@Param("utenteId") Long utenteId, @Param("prodottoId") Integer prodottoId);
}
