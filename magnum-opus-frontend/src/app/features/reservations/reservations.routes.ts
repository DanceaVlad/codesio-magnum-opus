import { Routes } from '@angular/router';

export const reservationRoutes: Routes = [
  {
    path: 'new',
    loadComponent: () =>
      import('./reservation-form/reservation-form').then((m) => m.ReservationForm),
  },
  {
    path: '**',
    loadComponent: () => import('../../pages/not-found/not-found').then((m) => m.NotFound),
  },
];
