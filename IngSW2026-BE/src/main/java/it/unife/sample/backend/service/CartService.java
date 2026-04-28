package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.CartDTO;
import it.unife.sample.backend.dto.CartUpdateRequestDTO;
import it.unife.sample.backend.mapper.CartMapper;
import it.unife.sample.backend.model.Cart;
import it.unife.sample.backend.model.CartItem;
import it.unife.sample.backend.model.Prodotto;
import it.unife.sample.backend.repository.ProdottoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
public class CartService {

    private static final String CART_SESSION_ATTRIBUTE = "cart";

    private final ProdottoRepository prodottoRepository;

    public CartService(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

    public CartDTO getCart(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        refreshCartDataFromDatabase(cart);
        return CartMapper.toDTO(cart);
    }

    public CartDTO addProduct(Long productId, HttpSession session) {
        Prodotto prodotto = findProductOrThrow(productId);
        Cart cart = getOrCreateCart(session);

        cart.addItem(productId, prodotto.getNome(), prodotto.getPrezzo().doubleValue());
        refreshCartDataFromDatabase(cart);
        return CartMapper.toDTO(cart);
    }

    public CartDTO removeProduct(Long productId, HttpSession session) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId non valido");
        }

        Cart cart = getOrCreateCart(session);
        cart.removeItem(productId);
        refreshCartDataFromDatabase(cart);
        return CartMapper.toDTO(cart);
    }

    public CartDTO updateQuantity(CartUpdateRequestDTO request, HttpSession session) {
        if (request == null || request.getProductId() == null || request.getQuantita() == null) {
            throw new IllegalArgumentException("productId e quantita sono obbligatori");
        }

        if (request.getQuantita() < 0) {
            throw new IllegalArgumentException("quantita non valida");
        }

        Cart cart = getOrCreateCart(session);

        if (request.getQuantita() == 0) {
            cart.removeItem(request.getProductId());
            refreshCartDataFromDatabase(cart);
            return CartMapper.toDTO(cart);
        }

        Prodotto prodotto = findProductOrThrow(request.getProductId());
        cart.updateQuantity(
                request.getProductId(),
                prodotto.getNome(),
                prodotto.getPrezzo().doubleValue(),
                request.getQuantita()
        );

        refreshCartDataFromDatabase(cart);
        return CartMapper.toDTO(cart);
    }

    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
        }
        return cart;
    }

    private Prodotto findProductOrThrow(Long productId) {
        Integer repositoryId = toRepositoryProductId(productId);
        return prodottoRepository.findById(repositoryId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));
    }

    private Optional<Prodotto> findProduct(Long productId) {
        if (productId == null || productId <= 0 || productId > Integer.MAX_VALUE) {
            return Optional.empty();
        }
        return prodottoRepository.findById(productId.intValue());
    }

    private Integer toRepositoryProductId(Long productId) {
        if (productId == null || productId <= 0 || productId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("productId non valido");
        }
        return productId.intValue();
    }

    private void refreshCartDataFromDatabase(Cart cart) {
        Iterator<CartItem> iterator = cart.getItems().iterator();

        while (iterator.hasNext()) {
            CartItem item = iterator.next();

            if (item.getQuantita() <= 0) {
                iterator.remove();
                continue;
            }

            Optional<Prodotto> optionalProduct = findProduct(item.getProductId());
            if (optionalProduct.isEmpty()) {
                iterator.remove();
                continue;
            }

            Prodotto prodotto = optionalProduct.get();
            item.setNome(prodotto.getNome());
            item.setPrezzo(prodotto.getPrezzo().doubleValue());
        }
    }
}