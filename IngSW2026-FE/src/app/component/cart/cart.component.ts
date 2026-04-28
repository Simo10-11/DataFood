import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../../service/cart.service';
import { OrderService } from '../../service/order.service';
import { Cart } from '../../dto/cart.model';
import { CartItem } from '../../dto/cart-item.model';
import { Order } from '../../dto/order.model';

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
  checkoutMessage = '';
  createdOrder: Order | null = null;

  constructor(
    private cartService: CartService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.loading = true;
    this.errorMessage = '';
    this.checkoutMessage = '';

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

  checkout(): void {
    if (!this.cart.items || this.cart.items.length === 0) {
      this.errorMessage = 'Carrello vuoto: aggiungi almeno un prodotto prima del checkout.';
      return;
    }

    this.errorMessage = '';
    this.checkoutMessage = '';

    this.orderService.checkout().subscribe({
      next: (order) => {
        this.createdOrder = order;
        this.checkoutMessage = 'Ordine confermato e messo in lavorazione.';
        this.cart = { items: [], totale: 0 };
      },
      error: (errorResponse) => {
        if (errorResponse?.status === 401) {
          this.errorMessage = 'Effettua il login per completare il checkout.';
          return;
        }

        if (errorResponse?.status === 400) {
          this.errorMessage = 'Carrello vuoto o dati non validi per il checkout.';
          return;
        }

        if (errorResponse?.status === 404) {
          this.errorMessage = 'Alcuni prodotti non esistono piu nel catalogo.';
          return;
        }

        this.errorMessage = 'Impossibile completare il checkout.';
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