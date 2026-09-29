import { Component, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-sparkline',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<div class="sparkline-placeholder"></div>`,
  styles: [`
    .sparkline-placeholder { width: 60px; height: 20px; background: #f3f4f6; border-radius: 4px; }
  `],
})
export class SparklineComponent {}
