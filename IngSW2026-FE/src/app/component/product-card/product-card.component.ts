import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Prodotto } from '../../dto/prodotto.model';
import { AuthService } from '../../service/auth.service';
import { WishlistService } from '../../service/wishlist.service';
import { CartService } from '../../service/cart.service';
import { Wishlist } from '../../dto/wishlist.model';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent {
  @Input({ required: true }) prodotto!: Prodotto;
  @Input() set isInWishlist(value: boolean) {
    this.wishlistSelected = value;
  }

  quantity = 1;
  wishlistSelected = false;
  utenteId?: number;
  cartMessage = '';
  cartErrorMessage = '';

  constructor(
    private wishlistService: WishlistService,
    private authService: AuthService,
    private cartService: CartService
  ) {
    this.utenteId = this.authService.getCurrentUser()?.id;
  }

  decrementQuantity(): void {
    if (this.quantity > 0) {
      this.quantity -= 1;
    }
  }

  incrementQuantity(): void {
    this.quantity += 1;
  }

  addToWishlist(prodottoId: number | undefined): void {
    this.utenteId = this.authService.getCurrentUser()?.id;

    // Guardia importante: evita chiamate con valori undefined.
    if (!this.utenteId || !prodottoId) {
      if (!this.utenteId) {
        this.showLoginRequiredPopup();
      }
      return;
    }

    const wishlist: Wishlist = {
      utenteId: this.utenteId,
      prodottoId: prodottoId
    };

    if (this.wishlistSelected) {
      this.wishlistService.removeFromWishlistByProduct(this.utenteId, prodottoId).subscribe({
        next: () => {
          this.wishlistSelected = false;
        },
        error: () => {
          this.wishlistSelected = true;
        }
      });
      return;
    }

    this.wishlistService.addToWishlist(wishlist).subscribe({
      next: () => {
        this.wishlistSelected = true;
      },
      error: () => {
        this.wishlistSelected = true;
      }
    });
  }

  addToCart(): void {
    this.utenteId = this.authService.getCurrentUser()?.id;

    if (!this.utenteId) {
      this.showLoginRequiredPopup();
      return;
    }

    if (!this.prodotto || this.prodotto.id === undefined || this.prodotto.id === null) {
      return;
    }

    const safeQuantity = Math.max(1, Math.floor(Number(this.quantity) || 1));
    this.cartMessage = '';
    this.cartErrorMessage = '';

    this.cartService.addToCart(this.prodotto.id, safeQuantity).subscribe({
      next: () => {
        this.cartMessage = 'Prodotto aggiunto al carrello.';
      },
      error: () => {
        this.cartErrorMessage = 'Impossibile aggiungere il prodotto al carrello.';
      }
    });
  }

  private showLoginRequiredPopup(): void {
    window.alert('Non puoi aggiungere prodotti se non sei loggato.');
  }
}
