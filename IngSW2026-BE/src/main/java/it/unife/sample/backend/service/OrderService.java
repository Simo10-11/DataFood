package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.OrderDTO;
import it.unife.sample.backend.mapper.OrderMapper;
import it.unife.sample.backend.model.Cart;
import it.unife.sample.backend.model.CartItem;
import it.unife.sample.backend.model.Ordine;
import it.unife.sample.backend.model.OrdineProdotto;
import it.unife.sample.backend.model.Prodotto;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.OrdineRepository;
import it.unife.sample.backend.repository.ProdottoRepository;
import it.unife.sample.backend.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private static final String CART_SESSION_ATTRIBUTE = "cart";
    private static final String LOGGED_USER_ID_SESSION_ATTRIBUTE = "loggedUserId";

    private final OrdineRepository ordineRepository;
    private final ProdottoRepository prodottoRepository;
    private final UtenteRepository utenteRepository;
    private final OrderMapper orderMapper;

    public OrderService(
            OrdineRepository ordineRepository,
            ProdottoRepository prodottoRepository,
            UtenteRepository utenteRepository,
            OrderMapper orderMapper
    ) {
        this.ordineRepository = ordineRepository;
        this.prodottoRepository = prodottoRepository;
        this.utenteRepository = utenteRepository;
        this.orderMapper = orderMapper;
    }

    public OrderDTO checkout(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrello vuoto");
        }

        Utente utente = getLoggedUser(session);

        Ordine ordine = new Ordine();
        ordine.setData(LocalDateTime.now());
        ordine.setStatus("in_lavorazione");
        ordine.setUtente(utente);

        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getQuantita() <= 0) {
                continue;
            }

            Integer productId = convertProductId(cartItem.getProductId());

            Prodotto prodotto = prodottoRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

            OrdineProdotto ordineProdotto = new OrdineProdotto();
            ordineProdotto.setOrdine(ordine);
            ordineProdotto.setProdotto(prodotto);
            ordineProdotto.setQuantita(cartItem.getQuantita());
            ordineProdotto.setPrezzoUnitario(prodotto.getPrezzo());

            ordine.getItems().add(ordineProdotto);
        }

        if (ordine.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrello vuoto");
        }

        // Forziamo il calcolo del totale prima del save per intercettare eventuali null lato item.
        calculateTotal(ordine);

        Ordine saved = ordineRepository.save(ordine);
        session.removeAttribute(CART_SESSION_ATTRIBUTE);
        return orderMapper.toDTO(saved);
    }

    public List<OrderDTO> getMyOrders(HttpSession session) {
        Utente utente = getLoggedUser(session);

        return ordineRepository.findByUtenteId(utente.getId()).stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    private Utente getLoggedUser(HttpSession session) {
        Object userIdObj = session.getAttribute(LOGGED_USER_ID_SESSION_ATTRIBUTE);
        if (userIdObj == null) {
            throw new IllegalStateException("Utente non autenticato");
        }

        Long userId;
        if (userIdObj instanceof Long longValue) {
            userId = longValue;
        } else if (userIdObj instanceof Integer intValue) {
            userId = intValue.longValue();
        } else {
            throw new IllegalStateException("Utente non autenticato");
        }

        return utenteRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Utente non autenticato"));
    }

    private Integer convertProductId(Long productId) {
        if (productId == null || productId <= 0 || productId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Prodotto non trovato");
        }
        return productId.intValue();
    }

    private BigDecimal calculateTotal(Ordine ordine) {
        return ordine.getItems().stream()
                .map(item -> item.getPrezzoUnitario().multiply(BigDecimal.valueOf(item.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}