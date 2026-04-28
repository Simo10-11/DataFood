import { OrderItem } from './order-item.model';

export interface Order {
  id: number;
  data: string;
  status: string;
  totale: number;
  items: OrderItem[];
}