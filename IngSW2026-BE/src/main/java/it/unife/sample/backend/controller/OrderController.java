package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.OrderDTO;
import it.unife.sample.backend.dto.OrderStatusUpdateDTO;
import it.unife.sample.backend.dto.CheckoutRequestDTO;
import it.unife.sample.backend.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping({"/api/orders", "/orders"})
// Gestisce le richieste relative agli ordini
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Completa il checkout dell'ordine usando la sessione corrente
    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(
            HttpSession session,
            @RequestBody(required = false) CheckoutRequestDTO request
    ) {
        try {
            boolean usePunti = request != null && request.isUsePunti();
            return ResponseEntity.ok(orderService.checkoutWithPoints(session, usePunti));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException exception) {
            if (isNotFoundError(exception)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Mostra in anteprima lo sconto applicabile all'ordine
    @GetMapping("/preview-discount")
    public ResponseEntity<?> previewDiscount(HttpSession session) {
        try {
            return ResponseEntity.ok(orderService.previewDiscount(session));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Restituisce gli ordini dell'utente loggato
    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getMyOrders(HttpSession session) {
        try {
            return ResponseEntity.ok(orderService.getMyOrders(session));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Restituisce tutti gli ordini con filtri e paginazione
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "data") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String search
    ) {
        try {
            return ResponseEntity.ok(orderService.getAllOrders(session, page, size, sortBy, direction, status, search));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Aggiorna lo stato di un ordine
    @PatchMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO request,
            HttpSession session
    ) {
        try {
            return ResponseEntity.ok(orderService.updateOrderStatus(id, request, session));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException exception) {
            if (isNotFoundError(exception)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isNotFoundError(IllegalArgumentException exception) {
        String message = exception.getMessage();
        return message != null && message.toLowerCase().contains("non trovato");
    }
}