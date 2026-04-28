import { OrderItem } from './order-item.model';

export interface Order {
  id: number;
  nomeCliente?: string;
  data: string;
  status: string;
  totale: number;
  items: OrderItem[];
}

export interface OrderStatusOption {
  value: string;
  label: string;
}