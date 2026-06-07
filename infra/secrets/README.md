# Secrets

Do not commit plaintext Kubernetes Secrets.

This deployment expects the Sealed Secrets controller to be installed by Argo CD before sealed secret manifests are synced.

## Cloudflare tunnel token

Create a remotely managed Cloudflare Tunnel in the Cloudflare dashboard. Choose Docker for the setup environment and copy only the `eyJ...` tunnel token from the generated command. Do not commit the plaintext token.

```sh
kubectl -n cloudflare create secret generic cloudflared-token \
  --from-literal=token='<cloudflare-tunnel-token>' \
  --dry-run=client -o yaml \
  | kubeseal --format=yaml \
  > infra/secrets/cloudflared-token.sealedsecret.yaml
```

After creating the sealed secret, add it to `infra/platform/cloudflared/kustomization.yaml` or to a dedicated secret Application.

## Cloudflare public hostnames

Configure these public hostnames on the Cloudflare Tunnel:

- `opus.codesio.com`
- `opus-auth.codesio.com`

Use these service targets:

- `opus-auth.codesio.com` -> `http://keycloak-service.auth.svc.cluster.local:8080`
- `opus.codesio.com` with path `^/api` -> `http://backend.app.svc.cluster.local:8080`
- `opus.codesio.com` -> `http://frontend.app.svc.cluster.local:8080`
