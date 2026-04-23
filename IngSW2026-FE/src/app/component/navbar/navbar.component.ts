import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  @Output() searchChange = new EventEmitter<string>();

  searchText = '';
  // Flag UI locale: quando true mostriamo i pulsanti di conferma logout.
  logoutConfirmationVisible = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  get currentUser() {
    // Lo leggiamo dal service cosi tutta la logica di persistenza resta centralizzata.
    return this.authService.getCurrentUser();
  }

  onSearchInput(): void {
    this.searchChange.emit(this.searchText);
  }

  requestLogout(): void {
    // Primo click su logout: non usciamo subito, chiediamo conferma.
    this.logoutConfirmationVisible = true;
  }

  cancelLogout(): void {
    // Se annulla, torniamo alla navbar compatta senza perdere utente.
    this.logoutConfirmationVisible = false;
  }

  confirmLogout(): void {
    // Logout confermato: pulizia stato locale e redirect al catalogo.
    this.authService.logout();
    this.logoutConfirmationVisible = false;
    this.router.navigateByUrl('/');
  }
}
