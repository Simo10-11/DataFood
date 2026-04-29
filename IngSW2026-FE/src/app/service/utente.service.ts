import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Utente } from '../dto/utente.model';

@Injectable({
  providedIn: 'root'
})
export class UtenteService {

  private readonly apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  getAllAdmin(): Observable<Utente[]> {
    return this.http.get<Utente[]>(this.apiUrl, { withCredentials: true });
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}`, { withCredentials: true });
  }
}