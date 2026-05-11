import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Wishlist } from '../dto/wishlist.model';

@Injectable({
  providedIn: 'root'
})
export class WishlistService {

  private readonly apiUrl = '/api/wishlist';
  wishlistUpdated$ = new Subject<void>();

  constructor(private http: HttpClient) {}

  getWishlistByUtente(utenteId: number): Observable<Wishlist[]> {
    if (!utenteId) {
      return of([]);
    }

    return this.http.get<Wishlist[]>(`${this.apiUrl}/${utenteId}`);
  }

  addToWishlist(wishlist: Wishlist): Observable<Wishlist> {
    return this.http.post<Wishlist>(this.apiUrl, wishlist).pipe(
      tap(() => this.wishlistUpdated$.next())
    );
  }

  removeFromWishlist(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.wishlistUpdated$.next())
    );
  }

  removeFromWishlistByProduct(utenteId: number, prodottoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}?utenteId=${utenteId}&prodottoId=${prodottoId}`).pipe(
      tap(() => this.wishlistUpdated$.next())
    );
  }
}
