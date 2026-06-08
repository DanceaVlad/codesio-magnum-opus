export interface RuntimeConfig {
  readonly apiBaseUrl: string;
  readonly oidcIssuer: string;
  readonly oidcClientId: string;
}

export const DEFAULT_RUNTIME_CONFIG: RuntimeConfig = {
  apiBaseUrl: '/api',
  oidcIssuer: 'https://opus-auth.codesio.com/realms/magnum-opus',
  oidcClientId: 'magnum-opus-frontend',
};
