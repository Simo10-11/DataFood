import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LeaderboardEntry {
  position: number;
  username: string;
  puntiTotali: number;
  badge: string;
}

export interface LeaderboardDTO {
  entries: LeaderboardEntry[];
}

export interface UserRankDTO {
  rank: number;
  username: string;
  puntiTotali: number;
}

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {
  private apiUrl = 'http://localhost:8080/api/leaderboard';

  constructor(private http: HttpClient) { }

  /**
   * Ottiene la leaderboard giornaliera (top 100)
   */
  getLeaderboard(): Observable<LeaderboardDTO> {
    return this.http.get<LeaderboardDTO>(this.apiUrl);
  }

  /**
   * Ottiene la posizione dell'utente nella leaderboard
   */
  getUserRank(utenteId: number): Observable<UserRankDTO> {
    return this.http.get<UserRankDTO>(`${this.apiUrl}/rank/${utenteId}`);
  }
}
