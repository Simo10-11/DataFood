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
  private readonly currentUserKey = 'currentUser';

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<Utente> {
    return this.http.post<Utente>(this.loginUrl, data);
  }

  saveCurrentUser(user: Utente): void {
    localStorage.setItem(this.currentUserKey, JSON.stringify(user));
  }

  getCurrentUser(): Utente | null {
    const storedUser = localStorage.getItem(this.currentUserKey);
    return storedUser ? JSON.parse(storedUser) as Utente : null;
  }

  logout(): void {
    localStorage.removeItem(this.currentUserKey);
  }
}