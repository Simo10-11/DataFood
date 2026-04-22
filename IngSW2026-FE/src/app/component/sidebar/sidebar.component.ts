import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Categoria } from '../../dto/categoria.model';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  @Input() categorie: Categoria[] = [];
  @Input() categoriaSelezionataId?: number;

  @Output() categoriaSelected = new EventEmitter<number | undefined>();

  selectCategoria(categoria: Categoria): void {
    // Gestisce sia id sia ID in modo robusto lato frontend.
    const id = categoria.id ?? categoria.ID;
    this.categoriaSelected.emit(id);
  }

  resetFiltro(): void {
    // undefined indica "tutte le categorie".
    this.categoriaSelected.emit(undefined);
  }
}
