import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="stat-card">
      <span class="stat-card__label">{{ label }}</span>
      <span class="stat-card__value">{{ value }}</span>
      @if (change !== undefined) {
        <span class="stat-card__change" [class.positive]="change >= 0" [class.negative]="change < 0">
          {{ change >= 0 ? '+' : '' }}{{ change | number:'1.2-2' }}%
        </span>
      }
    </div>
  `,
  styleUrl: './stat-card.component.scss',
})
export class StatCardComponent {
  @Input() label = '';
  @Input() value = '';
  @Input() change?: number;
}
