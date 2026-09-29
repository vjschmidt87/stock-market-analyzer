import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Stock } from '@core/models/stock.model';

@Component({
  selector: 'app-top-movers',
  standalone: true,
  imports: [CommonModule, RouterLink, DecimalPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './top-movers.component.html',
  styleUrl: './top-movers.component.scss',
})
export class TopMoversComponent {
  @Input() stocks: Stock[] = [];
  @Input() title = '';

  trackBySymbol(_: number, stock: Stock): string {
    return stock.symbol;
  }
}
