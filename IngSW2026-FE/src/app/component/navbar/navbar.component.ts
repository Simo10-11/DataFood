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
  logoutConfirmationVisible = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  get currentUser() {
    return this.authService.getCurrentUser();
  }

  onSearchInput(): void {
    this.searchChange.emit(this.searchText);
  }

  requestLogout(): void {
    this.logoutConfirmationVisible = true;
  }

  cancelLogout(): void {
    this.logoutConfirmationVisible = false;
  }

  confirmLogout(): void {
    this.authService.logout();
    this.logoutConfirmationVisible = false;
    this.router.navigateByUrl('/');
  }
}
