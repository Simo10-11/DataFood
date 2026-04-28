import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../dto/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private readonly apiUrl = '/api/orders';

  constructor(private http: HttpClient) {}

  checkout(): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/checkout`, {});
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/my`);
  }
}