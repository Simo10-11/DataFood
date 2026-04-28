package it.unife.sample.backend.model;

public class CartItem {

    private Long productId;
    private String nome;
    private double prezzo;
    private int quantita;

    public CartItem() {
    }

    public CartItem(Long productId, String nome, double prezzo, int quantita) {
        this.productId = productId;
        this.nome = nome;
        this.prezzo = prezzo;
        this.quantita = quantita;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
}