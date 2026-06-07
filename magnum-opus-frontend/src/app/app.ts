import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { RuntimeConfigService } from './runtime-config.service';

@Component({
  selector: 'cds-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('magnum-opus-frontend');
  protected readonly runtimeConfig = inject(RuntimeConfigService);
}
