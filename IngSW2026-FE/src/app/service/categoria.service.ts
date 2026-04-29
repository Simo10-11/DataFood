import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categoria } from '../dto/categoria.model';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private readonly apiUrl = '/api/categorie';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl);
  }

  getAllAdmin(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl, { withCredentials: true });
  }

  createCategory(category: Partial<Categoria>): Observable<Categoria> {
    return this.http.post<Categoria>(this.apiUrl, category, { withCredentials: true });
  }

  updateCategory(categoryId: number, category: Partial<Categoria>): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.apiUrl}/${categoryId}`, category, { withCredentials: true });
  }

  deleteCategory(categoryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${categoryId}`, { withCredentials: true });
  }
}
