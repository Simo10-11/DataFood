import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Cart } from '../dto/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private readonly apiUrl = '/api/cart';

  constructor(private http: HttpClient) {}

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl);
  }

  addToCart(productId: number, quantita = 1): Observable<Cart> {
    const safeProductId = Number(productId);
    const safeQuantita = Math.max(1, Math.floor(Number(quantita) || 1));

    let request$ = this.http.post<Cart>(`${this.apiUrl}/add/${safeProductId}`, {});

    for (let index = 1; index < safeQuantita; index += 1) {
      request$ = request$.pipe(
        switchMap(() => this.http.post<Cart>(`${this.apiUrl}/add/${safeProductId}`, {}))
      );
    }

    return request$;
  }

  removeFromCart(productId: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/remove/${productId}`, {});
  }

  updateQuantity(productId: number, quantita: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/update`, {
      productId,
      quantita
    });
  }
}