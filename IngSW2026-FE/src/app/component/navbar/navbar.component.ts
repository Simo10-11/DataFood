import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../service/auth.service';
import { CartService } from '../../service/cart.service';
import { WishlistService } from '../../service/wishlist.service';
import { LeaderboardService } from '../../service/leaderboard.service';
import { Utente } from '../../dto/utente.model';

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
  wishlistItemCount = 0;
  currentUser: Utente | null = null;
  userRank: number | null = null; // Posizione nella leaderboard
  // Flag UI locale: quando true mostriamo i pulsanti di conferma logout.
  logoutConfirmationVisible = false;
  private readonly subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private leaderboardService: LeaderboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser?.id) {
      this.loadUserRank(this.currentUser.id);
      this.loadWishlistCount(this.currentUser.id);
      this.loadCartCount();
    }

    this.subscriptions.add(
      this.authService.currentUser$.subscribe((user) => {
        this.currentUser = user;

        if (!user) {
          this.cartItemCount = 0;
          this.wishlistItemCount = 0;
          this.userRank = null;
          this.userMenuOpen = false;
          this.mobileMenuOpen = false;
          this.logoutConfirmationVisible = false;
          return;
        }

        this.loadCartCount();
        this.loadWishlistCount(user.id);
        this.loadUserRank(user.id);
      })
    );

    // Sottoscrizione agli aggiornamenti del carrello
    this.subscriptions.add(
      this.cartService.cartUpdated$.subscribe(() => {
        if (this.currentUser?.id) {
          this.loadCartCount();
        }
      })
    );

    // Sottoscrizione agli aggiornamenti della wishlist
    this.subscriptions.add(
      this.wishlistService.wishlistUpdated$.subscribe(() => {
        if (this.currentUser?.id) {
          this.loadWishlistCount(this.currentUser.id);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
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

  private loadWishlistCount(utenteId: number): void {
    this.subscriptions.add(
      this.wishlistService.getWishlistByUtente(utenteId).subscribe({
        next: (wishlist) => {
          this.wishlistItemCount = (wishlist ?? []).length;
        },
        error: () => {
          this.wishlistItemCount = 0;
        }
      })
    );
  }

  private loadUserRank(utenteId: number): void {
    this.subscriptions.add(
      this.leaderboardService.getUserRank(utenteId).subscribe({
        next: (data) => {
          this.userRank = data.rank;
        },
        error: (err) => {
          console.warn('Errore nel caricamento del rank:', err);
          this.userRank = null;
        }
      })
    );
  }
}
