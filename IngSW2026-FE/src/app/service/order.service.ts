import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../dto/order.model';

export interface OrderPageResponse {
  content: Order[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private readonly apiUrl = '/api/orders';

  constructor(private http: HttpClient) {}

  checkout(): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/checkout`, {}, { withCredentials: true });
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/my`, { withCredentials: true });
  }

  getAllOrders(params: {
    page?: number;
    size?: number;
    sortBy?: string;
    direction?: string;
    status?: string;
    search?: string;
  } = {}): Observable<OrderPageResponse> {
    const queryParams = new URLSearchParams();
    queryParams.set('page', String(params.page ?? 0));
    queryParams.set('size', String(params.size ?? 10));
    queryParams.set('sortBy', params.sortBy ?? 'data');
    queryParams.set('direction', params.direction ?? 'desc');
    queryParams.set('status', params.status ?? 'all');
    queryParams.set('search', params.search ?? '');

    return this.http.get<OrderPageResponse>(`${this.apiUrl}?${queryParams.toString()}`, { withCredentials: true });
  }

  updateOrderStatus(orderId: number, status: string): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/${orderId}`, { status }, { withCredentials: true });
  }
}