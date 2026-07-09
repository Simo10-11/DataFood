package it.unife.sample.backend.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Cart {

    // Modello in memoria che rappresenta il carrello dell'utente in sessione

    private List<CartItem> items = new ArrayList<>();

    // Restituisce tutti gli elementi presenti nel carrello
    public List<CartItem> getItems() {
        return items;
    }

    // Imposta gli elementi del carrello mantenendo una lista valida anche se il valore e nullo
    public void setItems(List<CartItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    // Aggiunge un prodotto al carrello o incrementa la quantita se esiste gia
    public void addItem(Long productId, String nome, double prezzo) {
        CartItem existingItem = findItem(productId);
        if (existingItem != null) {
            existingItem.setQuantita(existingItem.getQuantita() + 1);
            existingItem.setNome(nome);
            existingItem.setPrezzo(prezzo);
            return;
        }

        items.add(new CartItem(productId, nome, prezzo, 1));
    }

    // Rimuove un prodotto dal carrello
    public void removeItem(Long productId) {
        Iterator<CartItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (Objects.equals(item.getProductId(), productId)) {
                iterator.remove();
                return;
            }
        }
    }

    // Aggiorna la quantita di un prodotto nel carrello
    public void updateQuantity(Long productId, String nome, double prezzo, int quantita) {
        if (quantita <= 0) {
            removeItem(productId);
            return;
        }

        CartItem existingItem = findItem(productId);
        if (existingItem == null) {
            items.add(new CartItem(productId, nome, prezzo, quantita));
            return;
        }

        existingItem.setQuantita(quantita);
        existingItem.setNome(nome);
        existingItem.setPrezzo(prezzo);
    }

    // Calcola il totale complessivo del carrello
    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPrezzo() * item.getQuantita())
                .sum();
    }

    // Cerca un elemento del carrello per id prodotto
    private CartItem findItem(Long productId) {
        return items.stream()
                .filter(item -> Objects.equals(item.getProductId(), productId))
                .findFirst()
                .orElse(null);
    }
}