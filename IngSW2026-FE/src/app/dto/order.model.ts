import { OrderItem } from './order-item.model';
import { Utente } from './utente.model';

export interface Order {
  id: number;
  nomeCliente?: string;
  data: string;
  status: string;
  totale: number;
  items: OrderItem[];
  puntiGuadagnati?: number;
  puntiUtilizzati?: number;
  scontoApplicato?: number;
  utenteAggiornato?: Utente;
}

export interface OrderStatusOption {
  value: string;
  label: string;
}