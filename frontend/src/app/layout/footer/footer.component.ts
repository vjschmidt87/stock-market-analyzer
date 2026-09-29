import { Component, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <footer class="footer">
      <p>Stock Market Analyzer &mdash; Portfolio Project</p>
    </footer>
  `,
  styleUrl: './footer.component.scss',
})
export class FooterComponent {}
