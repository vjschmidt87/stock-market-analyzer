import { Component, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-portfolio-overview',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="portfolio-overview">
      <h3>Portfolio Overview</h3>
      <p>Sign in to see your watchlist summary here.</p>
    </div>
  `,
  styleUrl: './portfolio-overview.component.scss',
})
export class PortfolioOverviewComponent {}
