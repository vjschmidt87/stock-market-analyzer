import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { WatchlistService } from '@core/services/watchlist.service';
import { Watchlist } from '@core/models/stock.model';
import { StockRowComponent } from '../stock-row/stock-row.component';
import { LoadingSpinnerComponent } from '@shared/components/loading-spinner/loading-spinner.component';
import { ErrorMessageComponent } from '@shared/components/error-message/error-message.component';

@Component({
  selector: 'app-watchlist-view',
  standalone: true,
  imports: [CommonModule, FormsModule, StockRowComponent, LoadingSpinnerComponent, ErrorMessageComponent],
  templateUrl: './watchlist-view.component.html',
  styleUrl: './watchlist-view.component.scss',
})
export class WatchlistViewComponent implements OnInit {
  private readonly watchlistService = inject(WatchlistService);
  private readonly destroyRef = inject(DestroyRef);

  watchlists: Watchlist[] = [];
  loading = true;
  error = '';
  newWatchlistName = '';
  addSymbol: Record<number, string> = {};

  ngOnInit(): void {
    this.loadWatchlists();
  }

  createWatchlist(): void {
    if (!this.newWatchlistName.trim()) return;
    this.watchlistService.createWatchlist(this.newWatchlistName.trim())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(wl => {
        this.watchlists.push(wl);
        this.newWatchlistName = '';
      });
  }

  addStock(watchlistId: number): void {
    const symbol = this.addSymbol[watchlistId]?.trim();
    if (!symbol) return;
    this.watchlistService.addStock(watchlistId, symbol)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError(() => { this.error = 'Failed to add stock'; return of(null); })
      )
      .subscribe(updated => {
        if (updated) {
          const idx = this.watchlists.findIndex(w => w.id === watchlistId);
          if (idx >= 0) this.watchlists[idx] = updated;
          this.addSymbol[watchlistId] = '';
        }
      });
  }

  removeStock(watchlistId: number, symbol: string): void {
    this.watchlistService.removeStock(watchlistId, symbol)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(updated => {
        const idx = this.watchlists.findIndex(w => w.id === watchlistId);
        if (idx >= 0) this.watchlists[idx] = updated;
      });
  }

  deleteWatchlist(id: number): void {
    this.watchlistService.deleteWatchlist(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.watchlists = this.watchlists.filter(w => w.id !== id);
      });
  }

  trackById(_: number, wl: Watchlist): number {
    return wl.id;
  }

  private loadWatchlists(): void {
    this.watchlistService.getWatchlists()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError(() => { this.error = 'Failed to load watchlists'; return of([]); })
      )
      .subscribe(wls => {
        this.watchlists = wls;
        this.loading = false;
      });
  }
}
