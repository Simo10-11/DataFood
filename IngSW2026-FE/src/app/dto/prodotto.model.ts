export interface Prodotto {
  // Identificativo prodotto (allineato al campo id del DTO backend).
  id: number;
  // Nome mostrato nelle card del catalogo.
  nome: string;
  descrizione?: string;
  // Prezzo unitario in euro.
  prezzo: number;
  quantitaDisponibile: number;
  imageUrl: string | null;
  // Categoria in formato "piatto" per semplificare il consumo lato UI.
  idCategoria: number;
  nomeCategoria?: string;
}
