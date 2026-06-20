package it.unife.sample.backend.service;

import it.unife.sample.backend.dto.OrderDTO;
import it.unife.sample.backend.dto.OrderStatusUpdateDTO;
import it.unife.sample.backend.dto.UtenteDTO;
import it.unife.sample.backend.mapper.OrderMapper;
import it.unife.sample.backend.model.Cart;
import it.unife.sample.backend.model.CartItem;
import it.unife.sample.backend.model.Ordine;
import it.unife.sample.backend.model.OrdineProdotto;
import it.unife.sample.backend.model.OrderStatus;
import it.unife.sample.backend.model.Prodotto;
import it.unife.sample.backend.model.Utente;
import it.unife.sample.backend.repository.OrdineRepository;
import it.unife.sample.backend.repository.ProdottoRepository;
import it.unife.sample.backend.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    // Gestisce la creazione e la gestione degli ordini

    private static final String CART_SESSION_ATTRIBUTE = "cart";
    private static final String LOGGED_USER_ID_SESSION_ATTRIBUTE = "loggedUserId";

    private final OrdineRepository ordineRepository;
    private final ProdottoRepository prodottoRepository;
    private final UtenteRepository utenteRepository;
    private final OrderMapper orderMapper;
    private final PuntiService puntiService;

    public OrderService(
            OrdineRepository ordineRepository,
            ProdottoRepository prodottoRepository,
            UtenteRepository utenteRepository,
            OrderMapper orderMapper,
            PuntiService puntiService
    ) {
        this.ordineRepository = ordineRepository;
        this.prodottoRepository = prodottoRepository;
        this.utenteRepository = utenteRepository;
        this.orderMapper = orderMapper;
        this.puntiService = puntiService;
    }

    @Transactional
    public OrderDTO checkout(HttpSession session) {
        Cart cart = getCartFromSession(session);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrello vuoto");
        }

        Utente utente = getLoggedUser(session);

        Ordine ordine = new Ordine();
        ordine.setData(LocalDateTime.now());
        ordine.setStatus(OrderStatus.IN_LAVORAZIONE.getDbValue());
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
        BigDecimal totaleOrdine = calculateTotal(ordine);
        ordine.setTotalePagato(totaleOrdine);

        // Calcolo punti guadagnati
        int puntiGuadagnati = puntiService.calculatePuntiGuadagnati(totaleOrdine);

        Ordine saved = ordineRepository.save(ordine);

        // Aggiunta punti all'utente
        if (puntiGuadagnati > 0) {
            puntiService.aggiungiPunti(utente, puntiGuadagnati);
            // Ricarica l'utente per ottenere i punti aggiornati
            utente = utenteRepository.findById(utente.getId())
                    .orElseThrow(() -> new IllegalStateException("Utente non autenticato"));
        }

        session.removeAttribute(getCartSessionAttribute(session));

        OrderDTO result = orderMapper.toDTO(saved);
        result.setPuntiGuadagnati(puntiGuadagnati);
        result.setUtenteAggiornato(toUtenteDTO(utente));

        return result;
    }

    /**
     * Checkout con applicazione sconto punti
     * Se usePunti è true, applica il massimo sconto possibile usando i punti disponibili
     */
    @Transactional
    public OrderDTO checkoutWithPoints(HttpSession session, boolean usePunti) {
        Cart cart = getCartFromSession(session);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrello vuoto");
        }

        Utente utente = getLoggedUser(session);

        Ordine ordine = new Ordine();
        ordine.setData(LocalDateTime.now());
        ordine.setStatus(OrderStatus.IN_LAVORAZIONE.getDbValue());
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

        BigDecimal totaleOrdine = calculateTotal(ordine);

        // Applicazione sconto con punti
        int puntiUtilizzati = 0;
        double scontoApplicato = 0.0;

        if (usePunti && utente.getPuntiDisponibili() > 0) {
            PuntiService.DiscountResult discountResult = puntiService.applicaScontoWithPunti(
                    utente.getPuntiDisponibili(),
                    totaleOrdine
            );
            puntiUtilizzati = discountResult.puntiUtilizzati;
            scontoApplicato = discountResult.scontoApplicato;
            totaleOrdine = discountResult.nuovoTotale;
        }

        ordine.setTotalePagato(totaleOrdine);

        Ordine saved = ordineRepository.save(ordine);

        // Calcolo punti guadagnati sul totale DOPO lo sconto
        int puntiGuadagnati = puntiService.calculatePuntiGuadagnati(totaleOrdine);

        // Uso punti (se applicati)
        if (puntiUtilizzati > 0) {
            puntiService.usaPunti(utente, puntiUtilizzati);
        }

        // Aggiunta punti guadagnati
        if (puntiGuadagnati > 0) {
            puntiService.aggiungiPunti(utente, puntiGuadagnati);
        }

        // Ricarica l'utente per ottenere i punti aggiornati
        utente = utenteRepository.findById(utente.getId())
                .orElseThrow(() -> new IllegalStateException("Utente non autenticato"));

        session.removeAttribute(getCartSessionAttribute(session));

        OrderDTO result = orderMapper.toDTO(saved);
        result.setPuntiGuadagnati(puntiGuadagnati);
        result.setPuntiUtilizzati(puntiUtilizzati);
        result.setScontoApplicato(scontoApplicato);
        result.setUtenteAggiornato(toUtenteDTO(utente));

        return result;
    }

    /**
     * Restituisce preview dello sconto se l'utente usa i suoi punti
     */
    public java.util.Map<String, Object> previewDiscount(HttpSession session) {
        Cart cart = getCartFromSession(session);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrello vuoto");
        }

        Utente utente = getLoggedUser(session);

        // Calcola totale ordine
        BigDecimal totaleOrdine = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getQuantita() > 0) {
                totaleOrdine = totaleOrdine.add(
                        BigDecimal.valueOf(cartItem.getPrezzo())
                                .multiply(BigDecimal.valueOf(cartItem.getQuantita()))
                );
            }
        }

        // Calcola sconto disponibile
        PuntiService.DiscountResult discountResult = puntiService.applicaScontoWithPunti(
                utente.getPuntiDisponibili(),
                totaleOrdine
        );

        return java.util.Map.ofEntries(
                java.util.Map.entry("totaleOrdine", totaleOrdine.doubleValue()),
                java.util.Map.entry("puntiDisponibili", utente.getPuntiDisponibili()),
                java.util.Map.entry("valorePuntiInEuro", puntiService.convertPuntiToEuro(utente.getPuntiDisponibili())),
                java.util.Map.entry("scontoApplicabile", discountResult.scontoApplicato),
                java.util.Map.entry("totaleConSconto", discountResult.nuovoTotale.doubleValue()),
                java.util.Map.entry("puntiUtilizzabili", discountResult.puntiUtilizzati)
        );
    }

    public List<OrderDTO> getMyOrders(HttpSession session) {
        Utente utente = getLoggedUser(session);

        return ordineRepository.findByUtenteId(utente.getId()).stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public Page<OrderDTO> getAllOrders(
            HttpSession session,
            int page,
            int size,
            String sortBy,
            String direction,
            String statusFilter,
            String search
    ) {
        requireAdmin(session);

        List<Ordine> orders = new ArrayList<>(ordineRepository.findAll());
        List<Ordine> filteredOrders = applyFilters(orders, statusFilter, search);
        List<Ordine> sortedOrders = applySorting(filteredOrders, sortBy, direction);

        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int fromIndex = Math.min(safePage * safeSize, sortedOrders.size());
        int toIndex = Math.min(fromIndex + safeSize, sortedOrders.size());

        List<OrderDTO> content = sortedOrders.subList(fromIndex, toIndex).stream()
                .map(orderMapper::toDTO)
                .toList();

        return new PageImpl<>(content, PageRequest.of(safePage, safeSize), sortedOrders.size());
    }

    public OrderDTO updateOrderStatus(Long orderId, OrderStatusUpdateDTO request, HttpSession session) {
        requireAdmin(session);

        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Ordine non trovato");
        }

        if (request == null || request.getStatus() == null || !OrderStatus.isValid(request.getStatus())) {
            throw new IllegalArgumentException("Stato ordine non valido");
        }

        Ordine ordine = ordineRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato"));

        ordine.setStatus(OrderStatus.fromDbValue(request.getStatus()).getDbValue());
        Ordine saved = ordineRepository.save(ordine);
        return orderMapper.toDTO(saved);
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

    private Cart getCartFromSession(HttpSession session) {
        return (Cart) session.getAttribute(getCartSessionAttribute(session));
    }

    private String getCartSessionAttribute(HttpSession session) {
        Object userIdObj = session.getAttribute(LOGGED_USER_ID_SESSION_ATTRIBUTE);
        if (userIdObj instanceof Long longValue) {
            return CART_SESSION_ATTRIBUTE + ":user:" + longValue;
        }

        if (userIdObj instanceof Integer intValue) {
            return CART_SESSION_ATTRIBUTE + ":user:" + intValue.longValue();
        }

        return CART_SESSION_ATTRIBUTE + ":guest:" + session.getId();
    }

    private void requireAdmin(HttpSession session) {
        Utente utente = getLoggedUser(session);
        if (utente.getRuolo() == null || !"admin".equalsIgnoreCase(utente.getRuolo())) {
            throw new IllegalStateException("Utente non autorizzato");
        }
    }

    private List<Ordine> applyFilters(List<Ordine> orders, String statusFilter, String search) {
        return orders.stream()
                .filter(order -> matchesStatus(order, statusFilter))
                .filter(order -> matchesSearch(order, search))
                .collect(Collectors.toList());
    }

    private List<Ordine> applySorting(List<Ordine> orders, String sortBy, String direction) {
        Comparator<Ordine> comparator = buildComparator(sortBy);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return orders.stream().sorted(comparator).collect(Collectors.toList());
    }

    private Comparator<Ordine> buildComparator(String sortBy) {
        String safeSortBy = sortBy != null ? sortBy.toLowerCase(Locale.ROOT) : "data";

        return switch (safeSortBy) {
            case "status" -> Comparator.comparing(order -> order.getStatus() != null ? order.getStatus() : "");
            case "totale" -> Comparator.comparing(this::calculateTotal);
            default -> Comparator.comparing(order -> order.getData() != null ? order.getData() : LocalDateTime.MIN);
        };
    }

    private boolean matchesStatus(Ordine order, String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank() || "all".equalsIgnoreCase(statusFilter)) {
            return true;
        }

        return order.getStatus() != null && order.getStatus().equalsIgnoreCase(statusFilter.trim());
    }

    private boolean matchesSearch(Ordine order, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String normalized = search.trim().toLowerCase(Locale.ROOT);
        String orderId = order.getId() != null ? order.getId().toString() : "";
        String customerName = order.getUtente() != null
                ? ((order.getUtente().getNome() != null ? order.getUtente().getNome() : "") + " " +
                (order.getUtente().getCognome() != null ? order.getUtente().getCognome() : "")).toLowerCase(Locale.ROOT)
                : "";

        return orderId.contains(normalized) || customerName.contains(normalized);
    }

    private BigDecimal calculateTotal(Ordine order) {
        if (order == null) {
            return BigDecimal.ZERO;
        }

        if (order.getTotalePagato() != null) {
            return order.getTotalePagato();
        }

        if (order.getItems() == null) {
            return BigDecimal.ZERO;
        }

        return order.getItems().stream()
                .map(item -> item.getPrezzoUnitario().multiply(BigDecimal.valueOf(item.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer convertProductId(Long productId) {
        if (productId == null || productId <= 0 || productId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Prodotto non trovato");
        }
        return productId.intValue();
    }

    private UtenteDTO toUtenteDTO(Utente utente) {
        if (utente == null) {
            return null;
        }

        UtenteDTO dto = new UtenteDTO();
        dto.setId(utente.getId());
        dto.setEmail(utente.getEmail());
        dto.setNome(utente.getNome());
        dto.setCognome(utente.getCognome());
        dto.setRuolo(utente.getRuolo());
        dto.setPuntiDisponibili(utente.getPuntiDisponibili());
        return dto;
    }
}