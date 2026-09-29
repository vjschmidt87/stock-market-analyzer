import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '@env/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '@core/models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'stock_auth_token';
  private readonly userKey = 'stock_auth_user';

  private readonly loggedIn$ = new BehaviorSubject<boolean>(this.hasToken());
  private readonly user$ = new BehaviorSubject<AuthResponse | null>(this.getStoredUser());

  readonly isLoggedIn$ = this.loggedIn$.asObservable();
  readonly currentUser$ = this.user$.asObservable();

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap(response => this.handleAuth(response))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, request).pipe(
      tap(response => this.handleAuth(response))
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.loggedIn$.next(false);
    this.user$.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private handleAuth(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.userKey, JSON.stringify(response));
    this.loggedIn$.next(true);
    this.user$.next(response);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  private getStoredUser(): AuthResponse | null {
    const stored = localStorage.getItem(this.userKey);
    return stored ? JSON.parse(stored) : null;
  }
}
