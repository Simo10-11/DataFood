import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Prodotto } from '../dto/prodotto.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private readonly apiUrl = '/api/products';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Prodotto[]> {
    // GET catalogo completo.
    return this.http.get<Prodotto[]>(this.apiUrl);
  }

  searchByNome(nome: string | undefined): Observable<Prodotto[]> {
    const safeNome = nome ?? '';
    // GET ricerca per nome con query param ?nome=...
    const params = new HttpParams().set('nome', safeNome);
    return this.http.get<Prodotto[]>(`${this.apiUrl}/search`, { params });
  }

  getByCategoria(id: number | undefined): Observable<Prodotto[]> {
    if (id === undefined) {
      return of([]);
    }
    // GET prodotti filtrati per categoria.
    return this.http.get<Prodotto[]>(`${this.apiUrl}/categoria/${id}`);
  }
}
