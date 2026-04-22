import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  @Output() searchChange = new EventEmitter<string>();

  searchText = '';

  onSearchInput(): void {
    this.searchChange.emit(this.searchText);
  }
}
