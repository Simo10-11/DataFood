import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { ProductCardComponent } from '../product-card/product-card.component';
import { ProductService } from '../../service/product.service';
import { CategoriaService } from '../../service/categoria.service';
import { WishlistService } from '../../service/wishlist.service';
import { AuthService } from '../../service/auth.service';
import { Prodotto } from '../../dto/prodotto.model';
import { Categoria } from '../../dto/categoria.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, NavbarComponent, SidebarComponent, ProductCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  prodotti: Prodotto[] = [];
  categorie: Categoria[] = [];
  categoriaSelezionataId?: number;
  searchTerm = '';
  wishlistProductIds = new Set<number>();
  private authSubscription?: Subscription;

  constructor(
    private productService: ProductService,
    private categoriaService: CategoriaService,
    private wishlistService: WishlistService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Carica dati iniziali della homepage catalogo.
    this.loadProdotti();
    this.loadCategorie();
    this.loadWishlistState();

    this.authSubscription = this.authService.currentUser$.subscribe(() => {
      this.loadWishlistState();
    });
  }

  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
  }

  private loadWishlistState(): void {
    const utenteId = this.authService.getCurrentUser()?.id;
    if (!utenteId) {
      this.wishlistProductIds.clear();
      return;
    }

    this.wishlistService.getWishlistByUtente(utenteId).subscribe({
      next: (rows) => {
        this.wishlistProductIds = new Set(
          rows
            .map((item) => item.prodottoId)
            .filter((id): id is number => !!id)
        );
      },
      error: () => {
        // Se la chiamata fallisce, evitiamo blocchi UI e mostriamo cuori non attivi.
        this.wishlistProductIds.clear();
      }
    });
  }

  loadProdotti(): void {
    this.productService.getAll().subscribe((data) => {
      this.prodotti = data;
    });
  }

  loadCategorie(): void {
    this.categoriaService.getAll().subscribe((data) => {
      this.categorie = data;
    });
  }

  filterByCategoria(id: number | undefined): void {
    // Guardia importante: evita chiamate con id undefined.
    if (id === undefined) {
      this.categoriaSelezionataId = undefined;
      this.applyCurrentFilters();
      return;
    }

    this.categoriaSelezionataId = id;
    this.applyCurrentFilters();
  }

  onSearchChange(value: string): void {
    // Aggiorna il testo della ricerca dalla navbar.
    this.searchTerm = value;
    this.applyCurrentFilters();
  }

  private applyCurrentFilters(): void {
    // Applica in cascata i filtri correnti (categoria + ricerca testuale).
    const normalizedSearch = this.searchTerm.trim();

    if (this.categoriaSelezionataId === undefined && normalizedSearch.length === 0) {
      this.loadProdotti();
      return;
    }

    if (this.categoriaSelezionataId !== undefined) {
      // Se e selezionata una categoria, prima filtriamo lato backend per categoria.
      this.productService.getByCategoria(this.categoriaSelezionataId).subscribe((filteredByCategory) => {
        if (normalizedSearch.length === 0) {
          this.prodotti = filteredByCategory;
          return;
        }

        // Poi rifiniamo lato frontend per testo, senza una seconda chiamata HTTP.
        const lowerSearch = normalizedSearch.toLowerCase();
        this.prodotti = filteredByCategory.filter((prodotto) =>
          prodotto.nome.toLowerCase().includes(lowerSearch)
        );
      });
      return;
    }

    this.productService.searchByNome(normalizedSearch).subscribe((filteredBySearch) => {
      this.prodotti = filteredBySearch;
    });
  }

  get titoloCatalogo(): string {
    if (this.categoriaSelezionataId === undefined) {
      return 'Tutti i prodotti';
    }

    const categoria = this.categorie.find(
      (item) => (item.id ?? item.ID) === this.categoriaSelezionataId
    );

    return categoria?.nome ?? 'Categoria selezionata';
  }
}
