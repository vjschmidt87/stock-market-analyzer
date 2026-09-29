import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { Router } from '@angular/router';
import { Stock } from '@core/models/stock.model';

@Component({
  selector: 'app-stock-table',
  standalone: true,
  imports: [CommonModule, DecimalPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './stock-table.component.html',
  styleUrl: './stock-table.component.scss',
})
export class StockTableComponent {
  @Input() stocks: Stock[] = [];

  constructor(private readonly router: Router) {}

  trackBySymbol(_: number, stock: Stock): string {
    return stock.symbol;
  }

  goToStock(symbol: string): void {
    this.router.navigate(['/stocks', symbol]);
  }

  formatMarketCap(value: number): string {
    if (value >= 1_000_000_000_000) return `$${(value / 1_000_000_000_000).toFixed(1)}T`;
    if (value >= 1_000_000_000) return `$${(value / 1_000_000_000).toFixed(1)}B`;
    return `$${(value / 1_000_000).toFixed(0)}M`;
  }

  formatVolume(value: number): string {
    if (value >= 1_000_000) return `${(value / 1_000_000).toFixed(1)}M`;
    if (value >= 1_000) return `${(value / 1_000).toFixed(0)}K`;
    return `${value}`;
  }
}
