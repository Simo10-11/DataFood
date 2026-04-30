import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../service/auth.service';
import { CartService } from '../../service/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit, OnDestroy {
  @Output() searchChange = new EventEmitter<string>();

  searchText = '';
  userMenuOpen = false;
  mobileMenuOpen = false;
  cartItemCount = 0;
  // Flag UI locale: quando true mostriamo i pulsanti di conferma logout.
  logoutConfirmationVisible = false;
  private readonly subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subscriptions.add(
      this.authService.currentUser$.subscribe((user) => {
        if (!user) {
          this.cartItemCount = 0;
          this.userMenuOpen = false;
          this.mobileMenuOpen = false;
          this.logoutConfirmationVisible = false;
          return;
        }

        this.loadCartCount();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  get currentUser() {
    // Lo leggiamo dal service cosi tutta la logica di persistenza resta centralizzata.
    return this.authService.getCurrentUser();
  }

  onSearchInput(): void {
    this.searchChange.emit(this.searchText);
  }

  toggleUserMenu(): void {
    this.userMenuOpen = !this.userMenuOpen;
    this.mobileMenuOpen = false;
    this.logoutConfirmationVisible = false;
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
    this.userMenuOpen = false;
    this.logoutConfirmationVisible = false;
  }

  closeMenus(): void {
    this.userMenuOpen = false;
    this.mobileMenuOpen = false;
    this.logoutConfirmationVisible = false;
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

  private loadCartCount(): void {
    this.subscriptions.add(
      this.cartService.getCart().subscribe({
        next: (cart) => {
          this.cartItemCount = (cart.items ?? []).reduce((total, item) => total + (Number(item.quantita) || 0), 0);
        },
        error: () => {
          this.cartItemCount = 0;
        }
      })
    );
  }
}
