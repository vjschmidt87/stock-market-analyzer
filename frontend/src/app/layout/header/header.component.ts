import { Component, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '@core/services/auth.service';
import { AuthResponse } from '@core/models/auth.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  isLoggedIn = false;
  user: AuthResponse | null = null;

  constructor() {
    this.authService.isLoggedIn$.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(val => this.isLoggedIn = val);
    this.authService.currentUser$.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(val => this.user = val);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
