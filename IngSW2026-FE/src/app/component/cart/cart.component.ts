import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../../service/cart.service';
import { Cart } from '../../dto/cart.model';
import { CartItem } from '../../dto/cart-item.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cart: Cart = { items: [], totale: 0 };
  loading = false;
  errorMessage = '';

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.loading = true;
    this.errorMessage = '';

    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart = this.normalizeCart(cart);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Impossibile caricare il carrello.';
      }
    });
  }

  removeItem(productId: number): void {
    this.errorMessage = '';
    this.cartService.removeFromCart(productId).subscribe({
      next: (cart) => {
        this.cart = this.normalizeCart(cart);
      },
      error: () => {
        this.errorMessage = 'Impossibile rimuovere il prodotto dal carrello.';
      }
    });
  }

  increaseQuantity(item: CartItem): void {
    const nextQuantity = (item.quantita ?? 0) + 1;
    this.updateItemQuantity(item.productId, nextQuantity);
  }

  decreaseQuantity(item: CartItem): void {
    const nextQuantity = Math.max(0, (item.quantita ?? 0) - 1);
    this.updateItemQuantity(item.productId, nextQuantity);
  }

  updateQuantityFromInput(productId: number, value: string): void {
    const parsed = Number(value);
    const safeQuantity = Number.isNaN(parsed) ? 1 : Math.max(0, Math.floor(parsed));
    this.updateItemQuantity(productId, safeQuantity);
  }

  private updateItemQuantity(productId: number, quantita: number): void {
    this.errorMessage = '';
    this.cartService.updateQuantity(productId, quantita).subscribe({
      next: (cart) => {
        this.cart = this.normalizeCart(cart);
      },
      error: () => {
        this.errorMessage = 'Impossibile aggiornare la quantita del prodotto.';
      }
    });
  }

  private normalizeCart(cart: Cart | null | undefined): Cart {
    if (!cart) {
      return { items: [], totale: 0 };
    }

    return {
      items: (cart.items ?? []).map((item) => ({
        productId: Number(item.productId),
        nome: item.nome ?? 'Prodotto',
        prezzo: Number(item.prezzo) || 0,
        quantita: Math.max(0, Number(item.quantita) || 0)
      })),
      totale: Number(cart.totale) || 0
    };
  }
}