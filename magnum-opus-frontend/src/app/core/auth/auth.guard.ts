import { CanMatchFn } from '@angular/router';

export const authGuard: CanMatchFn = () => {
  return false;
};
