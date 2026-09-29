import { Component, ChangeDetectionStrategy, Input } from '@angular/core';

@Component({
  selector: 'app-error-message',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<div class="error">{{ message }}</div>`,
  styles: [`
    .error {
      padding: 16px 24px;
      background: #fde8e8;
      color: #d32f2f;
      border-radius: 8px;
      font-size: 0.875rem;
      text-align: center;
    }
  `],
})
export class ErrorMessageComponent {
  @Input() message = 'An error occurred';
}
