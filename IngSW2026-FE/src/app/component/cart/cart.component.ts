import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CartService } from '../../service/cart.service';
import { OrderService, DiscountPreview } from '../../service/order.service';
import { AuthService } from '../../service/auth.service';
import { Cart } from '../../dto/cart.model';
import { CartItem } from '../../dto/cart-item.model';
import { Order } from '../../dto/order.model';
import { Utente } from '../../dto/utente.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cart: Cart = { items: [], totale: 0 };
  loading = false;
  errorMessage = '';
  checkoutMessage = '';
  createdOrder: Order | null = null;
  usePunti = false;
  discountPreview: DiscountPreview | null = null;
  currentUser: Utente | null = null;

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCart();
    this.currentUser = this.authService.getCurrentUser();
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

  onUsePuntiChange(): void {
    if (this.usePunti) {
      this.loadDiscountPreview();
    } else {
      this.discountPreview = null;
    }
  }

  loadDiscountPreview(): void {
    this.errorMessage = '';
    this.orderService.previewDiscount().subscribe({
      next: (preview) => {
        this.discountPreview = preview;
      },
      error: () => {
        this.errorMessage = 'Impossibile caricare l\'anteprima dello sconto.';
        this.usePunti = false;
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

    this.orderService.checkout(this.usePunti).subscribe({
      next: (order) => {
        this.createdOrder = order;

        if (order.utenteAggiornato) {
          this.authService.saveCurrentUser(order.utenteAggiornato);
          this.currentUser = order.utenteAggiornato;
        }
        
        // Costruisce messaggio di conferma con punti
        let message = `Ordine confermato e messo in lavorazione.`;
        if (order.puntiGuadagnati && order.puntiGuadagnati > 0) {
          message += ` Hai guadagnato ${order.puntiGuadagnati} punti!`;
        }
        if (order.scontoApplicato && order.scontoApplicato > 0) {
          message += ` Sconto applicato: -€${order.scontoApplicato.toFixed(2)}`;
        }
        
        this.checkoutMessage = message;
        this.cart = { items: [], totale: 0 };
        this.usePunti = false;
        this.discountPreview = null;
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