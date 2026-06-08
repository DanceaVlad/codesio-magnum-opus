import { computed, Service, signal } from '@angular/core';

import { DEFAULT_RUNTIME_CONFIG, RuntimeConfig } from './runtime-config';

@Service()
export class RuntimeConfigService {
  private readonly state = signal(DEFAULT_RUNTIME_CONFIG);
  private readonly loadedState = signal(false);

  readonly config = this.state.asReadonly();
  readonly loaded = this.loadedState.asReadonly();
  readonly apiBaseUrl = computed(() => this.state().apiBaseUrl);
  readonly oidcIssuer = computed(() => this.state().oidcIssuer);
  readonly oidcClientId = computed(() => this.state().oidcClientId);

  async load(): Promise<void> {
    try {
      const response = await fetch('/config/runtime-config.json', { cache: 'no-store' });

      if (!response.ok) {
        this.loadedState.set(true);
        return;
      }

      this.state.set(parseRuntimeConfig(await response.json()));
    } finally {
      this.loadedState.set(true);
    }
  }
}

function parseRuntimeConfig(value: unknown): RuntimeConfig {
  if (!isRuntimeConfig(value)) {
    return DEFAULT_RUNTIME_CONFIG;
  }

  return {
    apiBaseUrl: value.apiBaseUrl,
    oidcIssuer: value.oidcIssuer,
    oidcClientId: value.oidcClientId,
  };
}

function isRuntimeConfig(value: unknown): value is RuntimeConfig {
  if (typeof value !== 'object' || value === null) {
    return false;
  }

  const candidate = value as Partial<Record<keyof RuntimeConfig, unknown>>;

  return (
    typeof candidate.apiBaseUrl === 'string' &&
    typeof candidate.oidcIssuer === 'string' &&
    typeof candidate.oidcClientId === 'string'
  );
}
