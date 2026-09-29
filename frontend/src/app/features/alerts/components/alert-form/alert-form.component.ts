import { Component, ChangeDetectionStrategy, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AlertType } from '@core/models/stock.model';

export interface AlertFormData {
  symbol: string;
  alertType: AlertType;
  targetValue: number;
}

@Component({
  selector: 'app-alert-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './alert-form.component.html',
  styleUrl: './alert-form.component.scss',
})
export class AlertFormComponent {
  @Output() alertCreated = new EventEmitter<AlertFormData>();

  symbol = '';
  alertType: AlertType = 'PRICE_ABOVE';
  targetValue: number | null = null;

  readonly alertTypes: { value: AlertType; label: string }[] = [
    { value: 'PRICE_ABOVE', label: 'Price Above' },
    { value: 'PRICE_BELOW', label: 'Price Below' },
    { value: 'PERCENT_CHANGE_UP', label: '% Change Up' },
    { value: 'PERCENT_CHANGE_DOWN', label: '% Change Down' },
  ];

  submit(): void {
    if (!this.symbol.trim() || this.targetValue === null) return;
    this.alertCreated.emit({
      symbol: this.symbol.trim().toUpperCase(),
      alertType: this.alertType,
      targetValue: this.targetValue,
    });
    this.symbol = '';
    this.targetValue = null;
  }
}
