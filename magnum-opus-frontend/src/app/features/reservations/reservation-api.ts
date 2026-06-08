import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';

@Service()
export class ReservationApi {
  private readonly http = inject(HttpClient);
}
