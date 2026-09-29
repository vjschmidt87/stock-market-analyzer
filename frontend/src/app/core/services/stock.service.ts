import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { AnalysisResult, MarketStats, PageResponse, Stock, StockSnapshot } from '@core/models/stock.model';

@Injectable({ providedIn: 'root' })
export class StockService {
  private readonly baseUrl = `${environment.apiUrl}/stocks`;

  constructor(private readonly http: HttpClient) {}

  getStocks(page = 0, size = 20): Observable<PageResponse<Stock>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Stock>>(this.baseUrl, { params });
  }

  getStock(symbol: string): Observable<Stock> {
    return this.http.get<Stock>(`${this.baseUrl}/${symbol}`);
  }

  searchStocks(query: string, page = 0, size = 20): Observable<PageResponse<Stock>> {
    const params = new HttpParams().set('q', query).set('page', page).set('size', size);
    return this.http.get<PageResponse<Stock>>(`${this.baseUrl}/search`, { params });
  }

  getSectors(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/sectors`);
  }

  getStocksBySector(sector: string, page = 0, size = 20): Observable<PageResponse<Stock>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Stock>>(`${this.baseUrl}/sector/${sector}`, { params });
  }

  getStockHistory(symbol: string, period = '3M'): Observable<StockSnapshot[]> {
    const params = new HttpParams().set('period', period);
    return this.http.get<StockSnapshot[]>(`${this.baseUrl}/${symbol}/history`, { params });
  }

  getAnalysis(symbol: string): Observable<AnalysisResult> {
    return this.http.get<AnalysisResult>(`${environment.apiUrl}/analysis/${symbol}`);
  }

  getByRecommendation(recommendation: string): Observable<AnalysisResult[]> {
    return this.http.get<AnalysisResult[]>(`${environment.apiUrl}/analysis/recommendation/${recommendation}`);
  }

  refreshAnalysis(symbol: string): Observable<AnalysisResult> {
    return this.http.post<AnalysisResult>(`${environment.apiUrl}/analysis/${symbol}/refresh`, {});
  }

  getMarketStats(): Observable<MarketStats> {
    return this.http.get<MarketStats>(`${environment.apiUrl}/market/stats`);
  }
}
