import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Alert, AlertType } from '@core/models/stock.model';

@Injectable({ providedIn: 'root' })
export class AlertService {
  private readonly baseUrl = `${environment.apiUrl}/alerts`;

  constructor(private readonly http: HttpClient) {}

  getAlerts(): Observable<Alert[]> {
    return this.http.get<Alert[]>(this.baseUrl);
  }

  createAlert(symbol: string, alertType: AlertType, targetValue: number): Observable<Alert> {
    return this.http.post<Alert>(this.baseUrl, { symbol, alertType, targetValue });
  }

  deleteAlert(alertId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${alertId}`);
  }
}
