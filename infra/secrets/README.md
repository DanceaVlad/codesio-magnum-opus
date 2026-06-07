# Secrets

Do not commit plaintext Kubernetes Secrets.

This deployment expects the Sealed Secrets controller to be installed by Argo CD before sealed secret manifests are synced.

## Cloudflare tunnel credentials

Create a locally managed Cloudflare Tunnel and keep the generated `credentials.json` private. Then create and seal the Kubernetes secret:

```sh
kubectl -n cloudflare create secret generic cloudflared-credentials \
  --from-file=credentials.json=/path/to/<tunnel-id>.json \
  --dry-run=client -o yaml \
  | kubeseal --format=yaml \
  > infra/secrets/cloudflared-credentials.sealedsecret.yaml
```

After creating the sealed secret, add it to `infra/platform/cloudflared/kustomization.yaml` or to a dedicated secret Application.

## Hostname placeholders

Replace these placeholders before syncing production:

- `opus.codesio.com`
- `auth.opus.codesio.com`
- `00000000-0000-0000-0000-000000000000`

Files that contain host placeholders:

- `infra/platform/cloudflared/configmap.yaml`
- `infra/platform/keycloak/keycloak.yaml`
- `infra/platform/keycloak/realm-import.yaml`
- `infra/apps/backend/kustomization.yaml`
- `infra/apps/frontend/runtime-config.json`
- `magnum-opus-frontend/public/config/runtime-config.json`
