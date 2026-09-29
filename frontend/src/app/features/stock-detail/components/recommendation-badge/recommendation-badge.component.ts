import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recommendation-badge',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="badge" [ngClass]="badgeClass">
      <span class="badge__label">{{ recommendation }}</span>
      <span class="badge__score">Score: {{ score }}/100</span>
    </div>
  `,
  styleUrl: './recommendation-badge.component.scss',
})
export class RecommendationBadgeComponent {
  @Input() recommendation = '';
  @Input() score = 0;

  get badgeClass(): string {
    return this.recommendation.toLowerCase().replace('_', '-');
  }
}
