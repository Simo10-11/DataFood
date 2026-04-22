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
}
