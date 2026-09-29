import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Watchlist } from '@core/models/stock.model';

@Injectable({ providedIn: 'root' })
export class WatchlistService {
  private readonly baseUrl = `${environment.apiUrl}/watchlists`;

  constructor(private readonly http: HttpClient) {}

  getWatchlists(): Observable<Watchlist[]> {
    return this.http.get<Watchlist[]>(this.baseUrl);
  }

  createWatchlist(name: string): Observable<Watchlist> {
    return this.http.post<Watchlist>(this.baseUrl, { name });
  }

  addStock(watchlistId: number, symbol: string): Observable<Watchlist> {
    return this.http.post<Watchlist>(`${this.baseUrl}/${watchlistId}/stocks/${symbol}`, {});
  }

  removeStock(watchlistId: number, symbol: string): Observable<Watchlist> {
    return this.http.delete<Watchlist>(`${this.baseUrl}/${watchlistId}/stocks/${symbol}`);
  }

  deleteWatchlist(watchlistId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${watchlistId}`);
  }
}
