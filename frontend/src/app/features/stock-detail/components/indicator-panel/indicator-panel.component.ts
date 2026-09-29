import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { AnalysisResult } from '@core/models/stock.model';

@Component({
  selector: 'app-indicator-panel',
  standalone: true,
  imports: [CommonModule, DecimalPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './indicator-panel.component.html',
  styleUrl: './indicator-panel.component.scss',
})
export class IndicatorPanelComponent {
  @Input() analysis!: AnalysisResult;

  getRsiClass(): string {
    if (!this.analysis?.rsi) return '';
    if (this.analysis.rsi < 30) return 'oversold';
    if (this.analysis.rsi > 70) return 'overbought';
    return 'neutral';
  }
}
