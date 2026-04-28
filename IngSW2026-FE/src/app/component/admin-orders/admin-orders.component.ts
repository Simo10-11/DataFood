import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Order, OrderStatusOption } from '../../dto/order.model';
import { OrderService, OrderPageResponse } from '../../service/order.service';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-orders.component.html',
  styleUrl: './admin-orders.component.scss'
})
export class AdminOrdersComponent implements OnInit {
  orders: Order[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

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

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
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
}