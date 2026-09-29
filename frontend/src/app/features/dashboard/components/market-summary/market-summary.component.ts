import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { StockService } from '@core/services/stock.service';
import { MarketStats } from '@core/models/stock.model';
import { StatCardComponent } from '@shared/components/stat-card/stat-card.component';
import { TopMoversComponent } from '../top-movers/top-movers.component';
import { LoadingSpinnerComponent } from '@shared/components/loading-spinner/loading-spinner.component';
import { ErrorMessageComponent } from '@shared/components/error-message/error-message.component';

@Component({
  selector: 'app-market-summary',
  standalone: true,
  imports: [CommonModule, StatCardComponent, TopMoversComponent, LoadingSpinnerComponent, ErrorMessageComponent],
  templateUrl: './market-summary.component.html',
  styleUrl: './market-summary.component.scss',
})
export class MarketSummaryComponent implements OnInit {
  private readonly stockService = inject(StockService);
  private readonly destroyRef = inject(DestroyRef);

  stats: MarketStats | null = null;
  loading = true;
  error = '';

  ngOnInit(): void {
    this.stockService.getMarketStats()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError(err => {
          this.error = 'Failed to load market data';
          this.loading = false;
          return of(null);
        })
      )
      .subscribe(stats => {
        this.stats = stats;
        this.loading = false;
      });
  }

  formatMarketCap(value: number): string {
    if (value >= 1_000_000_000_000) return `$${(value / 1_000_000_000_000).toFixed(1)}T`;
    if (value >= 1_000_000_000) return `$${(value / 1_000_000_000).toFixed(1)}B`;
    return `$${(value / 1_000_000).toFixed(1)}M`;
  }
}
