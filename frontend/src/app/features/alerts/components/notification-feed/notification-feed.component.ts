import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Alert } from '@core/models/stock.model';

@Component({
  selector: 'app-notification-feed',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (triggeredAlerts.length > 0) {
      <div class="notifications">
        <h3>Triggered Alerts</h3>
        @for (alert of triggeredAlerts; track alert.id) {
          <div class="notification">
            <span class="notification__symbol">{{ alert.stockSymbol }}</span>
            <span class="notification__type">{{ formatType(alert.alertType) }}</span>
            <span class="notification__value">{{ alert.targetValue }}</span>
          </div>
        }
      </div>
    }
  `,
  styleUrl: './notification-feed.component.scss',
})
export class NotificationFeedComponent {
  @Input() alerts: Alert[] = [];

  get triggeredAlerts(): Alert[] {
    return this.alerts.filter(a => a.triggered);
  }

  formatType(type: string): string {
    return type.replace(/_/g, ' ').toLowerCase().replace(/^\w/, c => c.toUpperCase());
  }
}
