import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LeaderboardService, LeaderboardEntry } from '../../service/leaderboard.service';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.scss']
})
export class LeaderboardComponent implements OnInit {
  leaderboard: LeaderboardEntry[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(private leaderboardService: LeaderboardService) { }

  ngOnInit(): void {
    this.loadLeaderboard();
  }

  loadLeaderboard(): void {
    this.isLoading = true;
    this.error = null;

    this.leaderboardService.getLeaderboard().subscribe({
      next: (data) => {
        this.leaderboard = data.entries;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento della leaderboard:', err);
        this.error = 'Errore nel caricamento della leaderboard';
        this.isLoading = false;
      }
    });
  }
}
