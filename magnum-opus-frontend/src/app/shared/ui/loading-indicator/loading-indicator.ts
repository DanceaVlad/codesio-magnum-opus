import { Component, input } from '@angular/core';

@Component({
  selector: 'cds-loading-indicator',
  templateUrl: './loading-indicator.html',
  styleUrl: './loading-indicator.css',
})
export class LoadingIndicator {
  readonly label = input('Loading');
}
