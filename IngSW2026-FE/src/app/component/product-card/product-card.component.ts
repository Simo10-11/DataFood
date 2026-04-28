import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Prodotto } from '../../dto/prodotto.model';
import { AuthService } from '../../service/auth.service';
import { WishlistService } from '../../service/wishlist.service';
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

  constructor(
    private wishlistService: WishlistService,
    private authService: AuthService
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
      return;
    }

    const wishlist: Wishlist = {
      utenteId: this.utenteId,
      prodottoId: prodottoId
    };

    this.wishlistService.addToWishlist(wishlist).subscribe({
      next: () => {
        this.wishlistSelected = true;
      },
      error: () => {
        // Se il backend rifiuta per duplicato, manteniamo il cuore attivo.
        this.wishlistSelected = true;
      }
    });
  }

  addToCart(): void {
    // Placeholder locale: conferma la quantita selezionata.
    if (this.quantity === 0) {
      return;
    }
  }
}
