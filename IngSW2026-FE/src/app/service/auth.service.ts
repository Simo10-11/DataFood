import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest } from '../dto/login-request.model';
import { Utente } from '../dto/utente.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly loginUrl = 'http://localhost:8080/auth/login';
  // Chiave unica del localStorage per evitare collisioni con altri dati dell'app.
  private readonly currentUserKey = 'currentUser';

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<Utente> {
    // Chiamata semplice al backend: niente token per ora, ci basta salvare i dati utente.
    return this.http.post<Utente>(this.loginUrl, data);
  }

  saveCurrentUser(user: Utente): void {
    // Salviamo l'utente cosi al refresh non perdiamo lo stato "loggato".
    localStorage.setItem(this.currentUserKey, JSON.stringify(user));
  }

  getCurrentUser(): Utente | null {
    const storedUser = localStorage.getItem(this.currentUserKey);
    // Se non c'e niente, restituiamo null per semplificare i controlli nei componenti.
    return storedUser ? JSON.parse(storedUser) as Utente : null;
  }

  logout(): void {
    // Logout lato frontend: per ora ci basta pulire lo storage locale.
    localStorage.removeItem(this.currentUserKey);
  }
}