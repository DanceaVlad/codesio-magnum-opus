import { Component, computed, input } from '@angular/core';

type ButtonVariant = 'primary' | 'secondary' | 'ghost';
type ButtonType = 'button' | 'submit' | 'reset';

@Component({
  selector: 'cds-button',
  templateUrl: './button.html',
  styleUrl: './button.css',
})
export class Button {
  readonly variant = input<ButtonVariant>('primary');
  readonly type = input<ButtonType>('button');
  readonly disabled = input(false);

  protected readonly classes = computed(() => {
    const base =
      'inline-flex min-h-10 items-center justify-center rounded-md px-4 py-2 text-sm font-semibold focus-visible:outline-2 focus-visible:outline-offset-2 disabled:pointer-events-none disabled:opacity-50';

    switch (this.variant()) {
      case 'primary':
        return `${base} bg-primary text-primary-foreground hover:bg-primary/90 focus-visible:outline-ring`;
      case 'secondary':
        return `${base} border border-border bg-card text-card-foreground hover:bg-secondary focus-visible:outline-ring`;
      case 'ghost':
        return `${base} text-foreground hover:bg-secondary focus-visible:outline-ring`;
    }
  });
}
