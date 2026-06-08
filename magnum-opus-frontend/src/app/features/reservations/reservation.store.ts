import { inject, Service } from '@angular/core';

import { ReservationApi } from './reservation-api';

@Service()
export class ReservationStore {
  private readonly api = inject(ReservationApi);
}
