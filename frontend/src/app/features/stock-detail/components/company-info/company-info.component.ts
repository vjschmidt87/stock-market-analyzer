import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of, switchMap } from 'rxjs';
import { StockService } from '@core/services/stock.service';
import { AnalysisResult, Stock, StockSnapshot } from '@core/models/stock.model';
import { PriceChartComponent } from '../price-chart/price-chart.component';
import { IndicatorPanelComponent } from '../indicator-panel/indicator-panel.component';
import { RecommendationBadgeComponent } from '../recommendation-badge/recommendation-badge.component';
import { LoadingSpinnerComponent } from '@shared/components/loading-spinner/loading-spinner.component';
import { ErrorMessageComponent } from '@shared/components/error-message/error-message.component';

@Component({
  selector: 'app-company-info',
  standalone: true,
  imports: [
    CommonModule, DecimalPipe, PriceChartComponent, IndicatorPanelComponent,
    RecommendationBadgeComponent, LoadingSpinnerComponent, ErrorMessageComponent,
  ],
  templateUrl: './company-info.component.html',
  styleUrl: './company-info.component.scss',
})
export class CompanyInfoComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly stockService = inject(StockService);
  private readonly destroyRef = inject(DestroyRef);

  stock: Stock | null = null;
  analysis: AnalysisResult | null = null;
  snapshots: StockSnapshot[] = [];
  loading = true;
  error = '';

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const symbol = params.get('symbol') ?? '';
        return forkJoin({
          stock: this.stockService.getStock(symbol),
          analysis: this.stockService.getAnalysis(symbol).pipe(catchError(() => of(null))),
          history: this.stockService.getStockHistory(symbol, '3M'),
        });
      }),
      takeUntilDestroyed(this.destroyRef),
      catchError(() => {
        this.error = 'Failed to load stock details';
        this.loading = false;
        return of(null);
      })
    ).subscribe(result => {
      if (result) {
        this.stock = result.stock;
        this.analysis = result.analysis;
        this.snapshots = result.history;
      }
      this.loading = false;
    });
  }

  onPeriodChange(period: string): void {
    if (!this.stock) return;
    this.stockService.getStockHistory(this.stock.symbol, period)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(snapshots => this.snapshots = snapshots);
  }

  formatMarketCap(value: number): string {
    if (value >= 1_000_000_000_000) return `$${(value / 1_000_000_000_000).toFixed(2)}T`;
    if (value >= 1_000_000_000) return `$${(value / 1_000_000_000).toFixed(2)}B`;
    return `$${(value / 1_000_000).toFixed(0)}M`;
  }

  formatVolume(value: number): string {
    if (value >= 1_000_000) return `${(value / 1_000_000).toFixed(1)}M`;
    return `${(value / 1_000).toFixed(0)}K`;
  }
}
