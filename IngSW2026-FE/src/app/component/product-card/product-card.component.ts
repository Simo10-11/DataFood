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
}
