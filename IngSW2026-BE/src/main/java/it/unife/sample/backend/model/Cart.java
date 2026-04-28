package it.unife.sample.backend.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Cart {

    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

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

    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPrezzo() * item.getQuantita())
                .sum();
    }

    private CartItem findItem(Long productId) {
        return items.stream()
                .filter(item -> Objects.equals(item.getProductId(), productId))
                .findFirst()
                .orElse(null);
    }
}