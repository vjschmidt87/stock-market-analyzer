import { Component, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  email = '';
  password = '';
  displayName = '';
  error = '';
  loading = false;

  register(): void {
    this.loading = true;
    this.error = '';
    this.authService.register({ email: this.email, password: this.password, displayName: this.displayName })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => this.router.navigate(['/']),
        error: (err) => {
          this.error = err.error?.message || 'Registration failed';
          this.loading = false;
        },
      });
  }
}
