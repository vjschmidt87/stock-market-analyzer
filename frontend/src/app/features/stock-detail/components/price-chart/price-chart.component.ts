import {
  Component, ChangeDetectionStrategy, Input, Output, EventEmitter,
  ElementRef, ViewChild, AfterViewInit, OnChanges, OnDestroy
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockSnapshot } from '@core/models/stock.model';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-price-chart',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './price-chart.component.html',
  styleUrl: './price-chart.component.scss',
})
export class PriceChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() snapshots: StockSnapshot[] = [];
  @Input() symbol = '';
  @Output() periodChange = new EventEmitter<string>();

  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  readonly periods = ['1W', '1M', '3M', '6M', '1Y'];
  selectedPeriod = '3M';
  private chart: Chart | null = null;

  ngAfterViewInit(): void {
    this.renderChart();
  }

  ngOnChanges(): void {
    if (this.chart) {
      this.renderChart();
    }
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }

  selectPeriod(period: string): void {
    this.selectedPeriod = period;
    this.periodChange.emit(period);
  }

  private renderChart(): void {
    if (!this.chartCanvas || !this.snapshots.length) return;
    this.chart?.destroy();

    const labels = this.snapshots.map(s => s.date);
    const data = this.snapshots.map(s => s.closePrice);

    this.chart = new Chart(this.chartCanvas.nativeElement, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: this.symbol,
          data,
          borderColor: '#1a73e8',
          backgroundColor: 'rgba(26, 115, 232, 0.1)',
          fill: true,
          tension: 0.3,
          pointRadius: 0,
          pointHitRadius: 10,
          borderWidth: 2,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            mode: 'index',
            intersect: false,
            callbacks: {
              label: (ctx) => `$${Number(ctx.raw).toFixed(2)}`,
            },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: { maxTicksLimit: 8, font: { size: 11 } },
          },
          y: {
            grid: { color: 'rgba(0,0,0,0.05)' },
            ticks: {
              callback: (val) => `$${val}`,
              font: { size: 11 },
            },
          },
        },
        interaction: { mode: 'nearest', axis: 'x', intersect: false },
      },
    });
  }
}
