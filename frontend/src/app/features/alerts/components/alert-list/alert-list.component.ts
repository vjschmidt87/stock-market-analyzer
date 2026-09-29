import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { AlertService } from '@core/services/alert.service';
import { Alert } from '@core/models/stock.model';
import { AlertFormComponent, AlertFormData } from '../alert-form/alert-form.component';
import { NotificationFeedComponent } from '../notification-feed/notification-feed.component';
import { LoadingSpinnerComponent } from '@shared/components/loading-spinner/loading-spinner.component';
import { ErrorMessageComponent } from '@shared/components/error-message/error-message.component';

@Component({
  selector: 'app-alert-list',
  standalone: true,
  imports: [CommonModule, AlertFormComponent, NotificationFeedComponent, LoadingSpinnerComponent, ErrorMessageComponent],
  templateUrl: './alert-list.component.html',
  styleUrl: './alert-list.component.scss',
})
export class AlertListComponent implements OnInit {
  private readonly alertService = inject(AlertService);
  private readonly destroyRef = inject(DestroyRef);

  alerts: Alert[] = [];
  loading = true;
  error = '';

  ngOnInit(): void {
    this.loadAlerts();
  }

  onAlertCreated(data: AlertFormData): void {
    this.alertService.createAlert(data.symbol, data.alertType, data.targetValue)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError(() => { this.error = 'Failed to create alert'; return of(null); })
      )
      .subscribe(alert => {
        if (alert) this.alerts.unshift(alert);
      });
  }

  deleteAlert(id: number): void {
    this.alertService.deleteAlert(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.alerts = this.alerts.filter(a => a.id !== id);
      });
  }

  trackById(_: number, alert: Alert): number {
    return alert.id;
  }

  formatType(type: string): string {
    return type.replace(/_/g, ' ').toLowerCase().replace(/^\w/, c => c.toUpperCase());
  }

  private loadAlerts(): void {
    this.alertService.getAlerts()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError(() => { this.error = 'Failed to load alerts'; return of([]); })
      )
      .subscribe(alerts => {
        this.alerts = alerts;
        this.loading = false;
      });
  }
}
