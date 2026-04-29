import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Order, OrderStatusOption } from '../../dto/order.model';
import { OrderService, OrderPageResponse } from '../../service/order.service';
import { ProductService } from '../../service/product.service';
import { CategoriaService } from '../../service/categoria.service';
import { UtenteService } from '../../service/utente.service';
import { AuthService } from '../../service/auth.service';
import { Categoria } from '../../dto/categoria.model';
import { Prodotto } from '../../dto/prodotto.model';
import { Utente } from '../../dto/utente.model';

type AdminSection = 'orders' | 'products' | 'categories' | 'users';

interface ProductFormModel {
  id?: number;
  nome: string;
  descrizione: string;
  prezzo: number | null;
  quantitaDisponibile: number;
  imageUrl: string;
  idCategoria?: number;
}

interface CategoryFormModel {
  id?: number;
  nome: string;
  descrizione: string;
}

interface UserViewModel {
  id: number;
  nome: string;
  cognome: string;
  email: string;
  ruolo: string;
}

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-orders.component.html',
  styleUrl: './admin-orders.component.scss'
})
export class AdminOrdersComponent implements OnInit {
  activeSection: AdminSection = 'orders';

  orders: Order[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  products: Prodotto[] = [];
  categories: Categoria[] = [];
  productLoading = false;
  productErrorMessage = '';
  productSuccessMessage = '';
  productSearchText = '';
  productCategoryFilter = 'all';
  productSortBy = 'nome';
  productDirection = 'asc';
  productForm: ProductFormModel = this.emptyProductForm();

  categoryLoading = false;
  categoryErrorMessage = '';
  categorySuccessMessage = '';
  categorySearchText = '';
  categoryDirection: 'asc' | 'desc' = 'asc';
  categoryForm: CategoryFormModel = this.emptyCategoryForm();

  users: UserViewModel[] = [];
  userLoading = false;
  userErrorMessage = '';
  userSuccessMessage = '';
  userSearchText = '';
  userRoleFilter = 'all';
  userSortBy = 'nome';
  userDirection: 'asc' | 'desc' = 'asc';

  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  sortBy = 'data';
  direction = 'desc';
  statusFilter = 'all';
  searchText = '';

  readonly statusOptions: OrderStatusOption[] = [
    { value: 'all', label: 'Tutti gli stati' },
    { value: 'in_lavorazione', label: 'In lavorazione' },
    { value: 'completato', label: 'Completato' },
    { value: 'annullato', label: 'Annullato' }
  ];

  readonly sortOptions = [
    { value: 'data', label: 'Data' },
    { value: 'status', label: 'Stato' },
    { value: 'totale', label: 'Totale' }
  ];

  constructor(
    private orderService: OrderService,
    private productService: ProductService,
    private categoriaService: CategoriaService,
    private utenteService: UtenteService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.loadProducts();
    this.loadCategories();
    this.loadUsers();
  }

  switchSection(section: AdminSection): void {
    this.activeSection = section;
  }

  get currentUserId(): number | undefined {
    return this.authService.getCurrentUser()?.id;
  }

  loadOrders(): void {
    this.loading = true;
    this.errorMessage = '';

    this.orderService.getAllOrders({
      page: this.page,
      size: this.size,
      sortBy: this.sortBy,
      direction: this.direction,
      status: this.statusFilter,
      search: this.searchText
    }).subscribe({
      next: (response: OrderPageResponse) => {
        this.orders = response.content ?? [];
        this.totalPages = response.totalPages ?? 0;
        this.totalElements = response.totalElements ?? 0;
        this.page = response.number ?? 0;
        this.size = response.size ?? this.size;
        this.loading = false;
      },
      error: (errorResponse) => {
        this.loading = false;
        if (errorResponse?.status === 403) {
          this.errorMessage = 'Non sei autorizzato a visualizzare il pannello admin.';
          return;
        }

        this.errorMessage = 'Impossibile caricare gli ordini.';
      }
    });
  }

  applyFilters(): void {
    this.page = 0;
    this.loadOrders();
  }

  changePage(delta: number): void {
    const nextPage = this.page + delta;
    if (nextPage < 0 || nextPage >= this.totalPages) {
      return;
    }

    this.page = nextPage;
    this.loadOrders();
  }

  updateStatus(order: Order, newStatus: string): void {
    if (!order.id) {
      return;
    }

    if (order.status === newStatus) {
      return;
    }

    const confirmed = window.confirm(`Confermi il cambio stato dell'ordine #${order.id}?`);
    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.orderService.updateOrderStatus(order.id, newStatus).subscribe({
      next: (updatedOrder) => {
        this.successMessage = `Ordine #${updatedOrder.id} aggiornato con successo.`;
        this.loadOrders();
      },
      error: (errorResponse) => {
        if (errorResponse?.status === 400) {
          this.errorMessage = 'Stato non valido.';
          return;
        }

        if (errorResponse?.status === 403) {
          this.errorMessage = 'Non sei autorizzato ad aggiornare l' + "'" + 'ordine.';
          return;
        }

        this.errorMessage = 'Impossibile aggiornare lo stato dell\'ordine.';
      }
    });
  }

  trackByOrderId(_: number, order: Order): number {
    return order.id;
  }

  getStatusClass(status: string | undefined): string {
    switch ((status ?? '').toLowerCase()) {
      case 'completato':
        return 'status-completed';
      case 'annullato':
        return 'status-cancelled';
      default:
        return 'status-processing';
    }
  }

  loadProducts(): void {
    this.productLoading = true;
    this.productErrorMessage = '';

    this.productService.getAllAdmin().subscribe({
      next: (items) => {
        this.products = (items ?? []).map((product) => this.normalizeProduct(product));
        this.productLoading = false;
      },
      error: (errorResponse) => {
        this.productLoading = false;
        if (errorResponse?.status === 403) {
          this.productErrorMessage = 'Non sei autorizzato a visualizzare i prodotti admin.';
          return;
        }

        this.productErrorMessage = 'Impossibile caricare i prodotti.';
      }
    });
  }

  loadCategories(): void {
    this.categoryLoading = true;
    this.categoryErrorMessage = '';

    this.categoriaService.getAllAdmin().subscribe({
      next: (items) => {
        this.categories = items ?? [];
        this.categoryLoading = false;
      },
      error: (errorResponse) => {
        this.categories = [];
        this.categoryLoading = false;

        if (errorResponse?.status === 403) {
          this.categoryErrorMessage = 'Non sei autorizzato a visualizzare le categorie admin.';
          return;
        }

        this.categoryErrorMessage = 'Impossibile caricare le categorie.';
      }
    });
  }

  loadUsers(): void {
    this.userLoading = true;
    this.userErrorMessage = '';

    this.utenteService.getAllAdmin().subscribe({
      next: (items) => {
        this.users = (items ?? []).map((user) => this.normalizeUser(user));
        this.userLoading = false;
      },
      error: (errorResponse) => {
        this.userLoading = false;

        if (errorResponse?.status === 403) {
          this.userErrorMessage = 'Non sei autorizzato a visualizzare gli utenti admin.';
          return;
        }

        this.userErrorMessage = 'Impossibile caricare gli utenti.';
      }
    });
  }

  get visibleUsers(): UserViewModel[] {
    const normalizedSearch = this.userSearchText.trim().toLowerCase();
    const selectedRole = this.userRoleFilter;

    const sorted = [...this.users]
      .filter((user) => {
        if (!normalizedSearch) {
          return true;
        }

        return user.nome.toLowerCase().includes(normalizedSearch)
          || user.cognome.toLowerCase().includes(normalizedSearch)
          || user.email.toLowerCase().includes(normalizedSearch);
      })
      .filter((user) => {
        if (selectedRole === 'all') {
          return true;
        }

        return user.ruolo === selectedRole;
      })
      .sort((left, right) => {
        let comparison = 0;

        switch (this.userSortBy) {
          case 'email':
            comparison = left.email.localeCompare(right.email);
            break;
          case 'ruolo':
            comparison = left.ruolo.localeCompare(right.ruolo);
            break;
          default:
            comparison = `${left.nome} ${left.cognome}`.localeCompare(`${right.nome} ${right.cognome}`);
            break;
        }

        return this.userDirection === 'desc' ? -comparison : comparison;
      });

    return sorted;
  }

  applyUserFilters(): void {
    // Filtri locali per mantenere la UI fluida e coerente.
  }

  deleteUser(user: UserViewModel): void {
    if (!user.id) {
      return;
    }

    if (this.currentUserId === user.id) {
      this.userErrorMessage = 'Non puoi eliminare il tuo account mentre sei loggato.';
      return;
    }

    const confirmed = window.confirm(`Eliminare l'utente ${user.nome} ${user.cognome}?`);
    if (!confirmed) {
      return;
    }

    this.userErrorMessage = '';
    this.userSuccessMessage = '';

    this.utenteService.deleteUser(user.id).subscribe({
      next: () => {
        this.userSuccessMessage = 'Utente eliminato con successo.';
        this.loadUsers();
      },
      error: (errorResponse) => {
        if (errorResponse?.status === 403) {
          this.userErrorMessage = 'Non sei autorizzato a eliminare questo utente.';
          return;
        }

        if (errorResponse?.status === 409) {
          this.userErrorMessage = 'Utente collegato a dati relazionali: eliminazione bloccata.';
          return;
        }

        if (errorResponse?.status === 400) {
          this.userErrorMessage = 'Non puoi eliminare il tuo account o un amministratore.';
          return;
        }

        if (errorResponse?.status === 404) {
          this.userErrorMessage = 'Utente non trovato.';
          return;
        }

        this.userErrorMessage = 'Impossibile eliminare l\'utente.';
      }
    });
  }

  isProtectedUser(user: UserViewModel): boolean {
    return this.currentUserId === user.id || user.ruolo.toLowerCase() === 'admin';
  }

  get visibleCategories(): Categoria[] {
    const normalizedSearch = this.categorySearchText.trim().toLowerCase();

    const sorted = [...this.categories]
      .filter((category) => {
        if (!normalizedSearch) {
          return true;
        }

        const nameMatch = category.nome.toLowerCase().includes(normalizedSearch);
        const descriptionMatch = (category.descrizione ?? '').toLowerCase().includes(normalizedSearch);
        return nameMatch || descriptionMatch;
      })
      .sort((left, right) => (left.nome ?? '').localeCompare(right.nome ?? ''));

    return this.categoryDirection === 'desc' ? sorted.reverse() : sorted;
  }

  applyCategoryFilters(): void {
    // Filtri locali per mantenere la UI fluida e consistente.
  }

  startCreateCategory(): void {
    this.categoryForm = this.emptyCategoryForm();
    this.categoryErrorMessage = '';
    this.categorySuccessMessage = '';
  }

  editCategory(category: Categoria): void {
    this.categoryForm = {
      id: category.id ?? category.ID,
      nome: category.nome ?? '',
      descrizione: category.descrizione ?? ''
    };

    this.categoryErrorMessage = '';
    this.categorySuccessMessage = '';
    this.switchSection('categories');
  }

  cancelEditingCategory(): void {
    this.startCreateCategory();
  }

  saveCategory(): void {
    if (!this.isCategoryFormValid()) {
      this.categoryErrorMessage = 'Il nome categoria e obbligatorio.';
      return;
    }

    const payload: Partial<Categoria> = {
      nome: this.categoryForm.nome.trim(),
      descrizione: this.categoryForm.descrizione?.trim() || ''
    };

    this.categoryErrorMessage = '';
    this.categorySuccessMessage = '';

    const request$ = this.categoryForm.id
      ? this.categoriaService.updateCategory(this.categoryForm.id, payload)
      : this.categoriaService.createCategory(payload);

    request$.subscribe({
      next: () => {
        this.categorySuccessMessage = this.categoryForm.id
          ? 'Categoria aggiornata con successo.'
          : 'Categoria creata con successo.';
        this.loadCategories();
        this.startCreateCategory();
      },
      error: (errorResponse) => {
        if (errorResponse?.status === 403) {
          this.categoryErrorMessage = 'Non sei autorizzato a modificare le categorie.';
          return;
        }

        if (errorResponse?.status === 404) {
          this.categoryErrorMessage = 'Categoria non trovata.';
          return;
        }

        if (errorResponse?.status === 400) {
          this.categoryErrorMessage = 'Nome non valido o gia esistente.';
          return;
        }

        this.categoryErrorMessage = 'Impossibile salvare la categoria.';
      }
    });
  }

  deleteCategory(category: Categoria): void {
    const categoryId = category.id ?? category.ID;
    if (!categoryId) {
      return;
    }

    const confirmed = window.confirm(`Eliminare la categoria "${category.nome}"?`);
    if (!confirmed) {
      return;
    }

    this.categoryErrorMessage = '';
    this.categorySuccessMessage = '';

    this.categoriaService.deleteCategory(categoryId).subscribe({
      next: () => {
        this.categorySuccessMessage = 'Categoria eliminata con successo.';
        this.loadCategories();
        if (this.categoryForm.id === categoryId) {
          this.startCreateCategory();
        }
      },
      error: (errorResponse) => {
        if (errorResponse?.status === 403) {
          this.categoryErrorMessage = 'Non sei autorizzato a eliminare le categorie.';
          return;
        }

        if (errorResponse?.status === 409) {
          this.categoryErrorMessage = 'Categoria collegata a prodotti: eliminazione bloccata.';
          return;
        }

        if (errorResponse?.status === 404) {
          this.categoryErrorMessage = 'Categoria non trovata.';
          return;
        }

        this.categoryErrorMessage = 'Impossibile eliminare la categoria.';
      }
    });
  }

  get visibleProducts(): Prodotto[] {
    const normalizedSearch = this.productSearchText.trim().toLowerCase();
    const selectedCategoryId = this.productCategoryFilter === 'all'
      ? undefined
      : Number(this.productCategoryFilter);

    return [...this.products]
      .filter((product) => {
        if (!normalizedSearch) {
          return true;
        }

        const nameMatch = product.nome.toLowerCase().includes(normalizedSearch);
        const descriptionMatch = (product.descrizione ?? '').toLowerCase().includes(normalizedSearch);
        return nameMatch || descriptionMatch;
      })
      .filter((product) => {
        if (selectedCategoryId === undefined || Number.isNaN(selectedCategoryId)) {
          return true;
        }
        return product.idCategoria === selectedCategoryId;
      })
      .sort((left, right) => this.compareProducts(left, right));
  }

  applyProductFilters(): void {
    // Filtri locali: il pannello resta reattivo e coerente con il catalogo.
  }

  startCreateProduct(): void {
    this.productForm = this.emptyProductForm();
    this.productSuccessMessage = '';
    this.productErrorMessage = '';
  }

  editProduct(product: Prodotto): void {
    this.productForm = {
      id: product.id,
      nome: product.nome ?? '',
      descrizione: product.descrizione ?? '',
      prezzo: product.prezzo ?? null,
      quantitaDisponibile: product.quantitaDisponibile ?? 0,
      imageUrl: product.imageUrl ?? '',
      idCategoria: product.idCategoria
    };
    this.productSuccessMessage = '';
    this.productErrorMessage = '';
    this.switchSection('products');
  }

  cancelEditingProduct(): void {
    this.startCreateProduct();
  }

  saveProduct(): void {
    if (!this.isProductFormValid()) {
      this.productErrorMessage = 'Compila tutti i campi obbligatori e verifica i valori.';
      return;
    }

    const payload: Partial<Prodotto> = {
      nome: this.productForm.nome.trim(),
      descrizione: this.productForm.descrizione?.trim() || '',
      prezzo: Number(this.productForm.prezzo),
      quantitaDisponibile: Number(this.productForm.quantitaDisponibile),
      imageUrl: this.productForm.imageUrl?.trim() || null,
      idCategoria: this.productForm.idCategoria
    } as Partial<Prodotto>;

    this.productErrorMessage = '';
    this.productSuccessMessage = '';

    const request$ = this.productForm.id
      ? this.productService.updateProduct(this.productForm.id, payload)
      : this.productService.createProduct(payload);

    request$.subscribe({
      next: () => {
        this.productSuccessMessage = this.productForm.id
          ? 'Prodotto aggiornato con successo.'
          : 'Prodotto creato con successo.';
        this.loadProducts();
        this.startCreateProduct();
      },
      error: (errorResponse) => {
        if (errorResponse?.status === 403) {
          this.productErrorMessage = 'Non sei autorizzato a modificare i prodotti.';
          return;
        }

        if (errorResponse?.status === 404) {
          this.productErrorMessage = 'Categoria o prodotto non trovato.';
          return;
        }

        this.productErrorMessage = 'Impossibile salvare il prodotto.';
      }
    });
  }

  deleteProduct(product: Prodotto): void {
    if (!product.id) {
      return;
    }

    const confirmed = window.confirm(`Eliminare definitivamente il prodotto "${product.nome}"?`);
    if (!confirmed) {
      return;
    }

    this.productErrorMessage = '';
    this.productSuccessMessage = '';

    this.productService.deleteProduct(product.id).subscribe({
      next: () => {
        this.productSuccessMessage = 'Prodotto eliminato con successo.';
        this.loadProducts();
        if (this.productForm.id === product.id) {
          this.startCreateProduct();
        }
      },
      error: (errorResponse) => {
        if (errorResponse?.status === 403) {
          this.productErrorMessage = 'Non sei autorizzato a eliminare i prodotti.';
          return;
        }

        if (errorResponse?.status === 404) {
          this.productErrorMessage = 'Prodotto non trovato.';
          return;
        }

        this.productErrorMessage = 'Impossibile eliminare il prodotto.';
      }
    });
  }

  getCategoryName(categoryId: number | undefined): string {
    if (categoryId === undefined) {
      return 'Categoria non disponibile';
    }

    const category = this.categories.find((item) => (item.id ?? item.ID) === categoryId);
    return category?.nome ?? 'Categoria non disponibile';
  }

  private compareProducts(left: Prodotto, right: Prodotto): number {
    let comparison = 0;

    switch (this.productSortBy) {
      case 'prezzo':
        comparison = (left.prezzo ?? 0) - (right.prezzo ?? 0);
        break;
      case 'categoria':
        comparison = this.getCategoryName(left.idCategoria).localeCompare(this.getCategoryName(right.idCategoria));
        break;
      default:
        comparison = (left.nome ?? '').localeCompare(right.nome ?? '');
        break;
    }

    return this.productDirection === 'desc' ? -comparison : comparison;
  }

  private isProductFormValid(): boolean {
    return !!this.productForm.nome?.trim()
      && this.productForm.prezzo !== null
      && Number(this.productForm.prezzo) > 0
      && Number(this.productForm.quantitaDisponibile) >= 0
      && this.productForm.idCategoria !== undefined;
  }

  private emptyProductForm(): ProductFormModel {
    return {
      nome: '',
      descrizione: '',
      prezzo: null,
      quantitaDisponibile: 0,
      imageUrl: '',
      idCategoria: undefined
    };
  }

  private emptyCategoryForm(): CategoryFormModel {
    return {
      nome: '',
      descrizione: ''
    };
  }

  private isCategoryFormValid(): boolean {
    return !!this.categoryForm.nome?.trim();
  }

  private normalizeProduct(product: Prodotto): Prodotto {
    return {
      ...product,
      id: Number(product.id),
      prezzo: Number(product.prezzo) || 0,
      quantitaDisponibile: Number(product.quantitaDisponibile) || 0,
      imageUrl: product.imageUrl ?? null,
      idCategoria: Number(product.idCategoria),
      nomeCategoria: product.nomeCategoria ?? this.getCategoryName(product.idCategoria)
    };
  }

  private normalizeUser(user: Utente): UserViewModel {
    return {
      id: Number(user.id),
      nome: user.nome ?? '',
      cognome: user.cognome ?? '',
      email: user.email ?? '',
      ruolo: (user.ruolo ?? 'cliente').toLowerCase()
    };
  }
}