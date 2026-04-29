import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Wishlist } from '../dto/wishlist.model';

@Injectable({
  providedIn: 'root'
})
export class WishlistService {

  private readonly apiUrl = '/api/wishlist';

  constructor(private http: HttpClient) {}

  getWishlistByUtente(utenteId: number): Observable<Wishlist[]> {
    if (!utenteId) {
      return of([]);
    }

    return this.http.get<Wishlist[]>(`${this.apiUrl}/${utenteId}`);
  }

  addToWishlist(wishlist: Wishlist): Observable<Wishlist> {
    return this.http.post<Wishlist>(this.apiUrl, wishlist);
  }

  removeFromWishlist(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  removeFromWishlistByProduct(utenteId: number, prodottoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}?utenteId=${utenteId}&prodottoId=${prodottoId}`);
  }
}
