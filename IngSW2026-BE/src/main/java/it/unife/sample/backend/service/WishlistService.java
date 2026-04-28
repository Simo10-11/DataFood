package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.WishlistDTO;
import it.unife.sample.backend.mapper.WishlistMapper;
import it.unife.sample.backend.model.Prodotto;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.model.Wishlist;
import it.unife.sample.backend.repository.ProdottoRepository;
import it.unife.sample.backend.repository.UtenteRepository;
import it.unife.sample.backend.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UtenteRepository utenteRepository,
            ProdottoRepository prodottoRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.utenteRepository = utenteRepository;
        this.prodottoRepository = prodottoRepository;
    }

    public List<WishlistDTO> getWishlistByUtente(Long utenteId) {
        return wishlistRepository.findByUtente_Id(utenteId).stream()
                .map(WishlistMapper::toDTO)
                .toList();
    }

    public WishlistDTO addToWishlist(WishlistDTO dto) {
        if (dto.getUtenteId() == null || dto.getProdottoId() == null) {
            throw new IllegalArgumentException("utenteId e prodottoId sono obbligatori");
        }

        boolean alreadyExists = wishlistRepository.existsByUtenteIdAndProdottoId(dto.getUtenteId(), dto.getProdottoId());
        if (alreadyExists) {
            throw new IllegalArgumentException("Prodotto gia presente nella wishlist");
        }

        Utente utente = utenteRepository.findById(dto.getUtenteId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Prodotto prodotto = prodottoRepository.findById(dto.getProdottoId())
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        Wishlist entity = WishlistMapper.toEntity(dto, utente, prodotto);
        Wishlist saved = wishlistRepository.save(entity);

        return WishlistMapper.toDTO(saved);
    }

    public void removeFromWishlist(Long id) {
        if (!wishlistRepository.existsById(id)) {
            throw new IllegalArgumentException("Elemento wishlist non trovato");
        }
        wishlistRepository.deleteById(id);
    }
}
