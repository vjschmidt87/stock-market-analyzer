import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, debounceTime, of, Subject, switchMap } from 'rxjs';
import { StockService } from '@core/services/stock.service';
import { Stock } from '@core/models/stock.model';
import { FilterPanelComponent } from '../filter-panel/filter-panel.component';
import { StockTableComponent } from '../stock-table/stock-table.component';
import { LoadingSpinnerComponent } from '@shared/components/loading-spinner/loading-spinner.component';
import { ErrorMessageComponent } from '@shared/components/error-message/error-message.component';

@Component({
  selector: 'app-screener-view',
  standalone: true,
  imports: [CommonModule, FilterPanelComponent, StockTableComponent, LoadingSpinnerComponent, ErrorMessageComponent],
  templateUrl: './screener-view.component.html',
  styleUrl: './screener-view.component.scss',
})
export class ScreenerViewComponent implements OnInit {
  private readonly stockService = inject(StockService);
  private readonly destroyRef = inject(DestroyRef);

  stocks: Stock[] = [];
  sectors: string[] = [];
  loading = true;
  error = '';
  currentPage = 0;
  totalPages = 0;

  private readonly searchSubject = new Subject<string>();

  ngOnInit(): void {
    this.loadStocks();
    this.loadSectors();

    this.searchSubject.pipe(
      debounceTime(300),
      switchMap(query => query
        ? this.stockService.searchStocks(query)
        : this.stockService.getStocks()
      ),
      takeUntilDestroyed(this.destroyRef),
      catchError(() => { this.error = 'Search failed'; return of(null); })
    ).subscribe(page => {
      if (page) {
        this.stocks = page.content;
        this.totalPages = page.totalPages;
      }
    });
  }

  onSearchChange(query: string): void {
    this.searchSubject.next(query);
  }

  onSectorChange(sector: string): void {
    this.loading = true;
    const request = sector
      ? this.stockService.getStocksBySector(sector)
      : this.stockService.getStocks();
    request.pipe(
      takeUntilDestroyed(this.destroyRef),
      catchError(() => { this.error = 'Failed to load stocks'; return of(null); })
    ).subscribe(page => {
      if (page) {
        this.stocks = page.content;
        this.totalPages = page.totalPages;
      }
      this.loading = false;
    });
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.loadStocks();
  }

  private loadStocks(): void {
    this.stockService.getStocks(this.currentPage)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError(() => { this.error = 'Failed to load stocks'; return of(null); })
      )
      .subscribe(page => {
        if (page) {
          this.stocks = page.content;
          this.totalPages = page.totalPages;
        }
        this.loading = false;
      });
  }

  private loadSectors(): void {
    this.stockService.getSectors()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(sectors => this.sectors = sectors);
  }
}
