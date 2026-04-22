export interface Categoria {
  // Campo standard atteso dal frontend.
  id?: number;
  // Fallback robusto se il backend restituisce ID maiuscolo.
  ID?: number;
  nome: string;
  descrizione?: string;
}
