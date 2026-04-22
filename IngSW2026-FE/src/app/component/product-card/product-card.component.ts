import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Prodotto } from '../../dto/prodotto.model';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent {
  @Input({ required: true }) prodotto!: Prodotto;

  quantity = 1;
  wishlistSelected = false;

  decrementQuantity(): void {
    if (this.quantity > 0) {
      this.quantity -= 1;
    }
  }

  incrementQuantity(): void {
    this.quantity += 1;
  }

  toggleWishlist(): void {
    // Placeholder locale per futura integrazione wishlist.
    this.wishlistSelected = !this.wishlistSelected;
  }

  addToCart(): void {
    // Placeholder locale: conferma la quantita selezionata.
    if (this.quantity === 0) {
      return;
    }
  }
}
