import { Component, ChangeDetectionStrategy, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { WatchlistStock } from '@core/models/stock.model';

@Component({
  selector: 'app-stock-row',
  standalone: true,
  imports: [CommonModule, RouterLink, DecimalPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="stock-row">
      <a [routerLink]="['/stocks', stock.symbol]" class="stock-row__info">
        <span class="stock-row__symbol">{{ stock.symbol }}</span>
        <span class="stock-row__name">{{ stock.companyName }}</span>
      </a>
      <div class="stock-row__price">
        <span class="stock-row__value">\${{ stock.currentPrice | number:'1.2-2' }}</span>
        <span class="stock-row__change" [class.positive]="stock.changePercent >= 0" [class.negative]="stock.changePercent < 0">
          {{ stock.changePercent >= 0 ? '+' : '' }}{{ stock.changePercent | number:'1.2-2' }}%
        </span>
      </div>
      <button class="stock-row__remove" (click)="remove.emit(stock.symbol)">Remove</button>
    </div>
  `,
  styleUrl: './stock-row.component.scss',
})
export class StockRowComponent {
  @Input() stock!: WatchlistStock;
  @Output() remove = new EventEmitter<string>();
}
