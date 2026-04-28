import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { WishlistService } from '../../service/wishlist.service';
import { AuthService } from '../../service/auth.service';
import { ProductService } from '../../service/product.service';
import { Wishlist } from '../../dto/wishlist.model';
import { Prodotto } from '../../dto/prodotto.model';

interface WishlistViewItem {
  wishlistId: number;
  prodottoId: number;
  nome: string;
  descrizione: string;
  prezzo: number;
  imageUrl: string | null;
}

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './wishlist.component.html',
  styleUrl: './wishlist.component.scss'
})
export class WishlistComponent implements OnInit {
  items: WishlistViewItem[] = [];
  loading = false;
  errorMessage = '';

  private utenteId?: number;

  constructor(
    private wishlistService: WishlistService,
    private productService: ProductService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    this.utenteId = currentUser?.id;

    if (!this.utenteId) {
      this.errorMessage = 'Effettua il login per visualizzare la wishlist.';
      return;
    }

    this.loadWishlist();
  }

  loadWishlist(): void {
    if (!this.utenteId) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.wishlistService.getWishlistByUtente(this.utenteId).subscribe({
      next: (wishlistRows) => {
        this.loadProductsForWishlist(wishlistRows);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Impossibile caricare la wishlist.';
      }
    });
  }

  removeFromWishlist(item: WishlistViewItem): void {
    if (!item.wishlistId) {
      return;
    }

    this.wishlistService.removeFromWishlist(item.wishlistId).subscribe({
      next: () => {
        this.items = this.items.filter((currentItem) => currentItem.wishlistId !== item.wishlistId);
      },
      error: () => {
        this.errorMessage = 'Impossibile rimuovere il prodotto dalla wishlist.';
      }
    });
  }

  private loadProductsForWishlist(wishlistRows: Wishlist[]): void {
    if (wishlistRows.length === 0) {
      this.items = [];
      this.loading = false;
      return;
    }

    this.productService.getAll().subscribe({
      next: (allProducts) => {
        this.items = this.mapWishlistRows(wishlistRows, allProducts);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Impossibile caricare i dettagli prodotti della wishlist.';
      }
    });
  }

  private mapWishlistRows(wishlistRows: Wishlist[], allProducts: Prodotto[]): WishlistViewItem[] {
    return wishlistRows
      .map((row) => {
        const product = allProducts.find((item) => item.id === row.prodottoId);
        if (!product || row.id === undefined) {
          return null;
        }

        return {
          wishlistId: row.id,
          prodottoId: row.prodottoId,
          nome: product.nome,
          descrizione: product.descrizione ?? 'Prodotto alimentare disponibile nel catalogo.',
          prezzo: product.prezzo,
          imageUrl: product.imageUrl
        };
      })
      .filter((item): item is WishlistViewItem => item !== null);
  }
}
