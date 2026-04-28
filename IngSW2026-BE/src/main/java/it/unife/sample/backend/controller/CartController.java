package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.CartDTO;
import it.unife.sample.backend.dto.CartUpdateRequestDTO;
import it.unife.sample.backend.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/cart", "/cart"})
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getCart(session));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<CartDTO> addToCart(@PathVariable Long productId, HttpSession session) {
        try {
            return ResponseEntity.ok(cartService.addProduct(productId, session));
        } catch (IllegalArgumentException exception) {
            if (isNotFoundError(exception)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(@PathVariable Long productId, HttpSession session) {
        try {
            return ResponseEntity.ok(cartService.removeProduct(productId, session));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<CartDTO> updateCart(
            @RequestBody CartUpdateRequestDTO request,
            HttpSession session
    ) {
        try {
            return ResponseEntity.ok(cartService.updateQuantity(request, session));
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