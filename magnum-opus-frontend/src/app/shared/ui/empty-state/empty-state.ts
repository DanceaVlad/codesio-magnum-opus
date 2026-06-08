import { Component, input } from '@angular/core';

@Component({
  selector: 'cds-empty-state',
  templateUrl: './empty-state.html',
  styleUrl: './empty-state.css',
})
export class EmptyState {
  readonly title = input('Nothing here yet');
  readonly description = input('');
}
