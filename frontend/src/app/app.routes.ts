import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('@features/dashboard/components/market-summary/market-summary.component')
        .then(m => m.MarketSummaryComponent),
  },
  {
    path: 'stocks',
    loadComponent: () =>
      import('@features/screener/components/screener-view/screener-view.component')
        .then(m => m.ScreenerViewComponent),
  },
  {
    path: 'stocks/:symbol',
    loadComponent: () =>
      import('@features/stock-detail/components/company-info/company-info.component')
        .then(m => m.CompanyInfoComponent),
  },
  {
    path: 'watchlists',
    canActivate: [authGuard],
    loadComponent: () =>
      import('@features/watchlist/components/watchlist-view/watchlist-view.component')
        .then(m => m.WatchlistViewComponent),
  },
  {
    path: 'alerts',
    canActivate: [authGuard],
    loadComponent: () =>
      import('@features/alerts/components/alert-list/alert-list.component')
        .then(m => m.AlertListComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('@features/auth/components/login/login.component')
        .then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('@features/auth/components/register/register.component')
        .then(m => m.RegisterComponent),
  },
  { path: '**', redirectTo: '' },
];
