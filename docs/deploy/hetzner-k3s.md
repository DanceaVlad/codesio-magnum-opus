# Hetzner k3s Deployment

This repo deploys Magnum Opus to a single-node k3s cluster on a Hetzner VPS, managed by Argo CD.

## What Gets Deployed

- Angular frontend, public through Cloudflare Tunnel.
- Spring Boot backend, public only through the frontend host `/api` route.
- Keycloak, public on its auth hostname for browser OIDC.
- App Postgres through CloudNativePG.
- Keycloak Postgres through CloudNativePG.
- Valkey as the Redis-compatible cache.
- Cloudflared as the only public entry path.

## Before Bootstrap

1. Point the repo remote used by Argo CD at a URL the VPS can read.
2. Confirm the public hostnames:
   - `opus.codesio.com`
   - `auth.opus.codesio.com`
3. Create a remotely managed Cloudflare Tunnel in the Cloudflare dashboard.
4. Configure the tunnel public hostnames:
   - `auth.opus.codesio.com` -> `http://keycloak-service.auth.svc.cluster.local:8080`
   - `opus.codesio.com` with path `^/api` -> `http://backend.app.svc.cluster.local:8080`
   - `opus.codesio.com` -> `http://frontend.app.svc.cluster.local:8080`
5. Copy the tunnel token from the Docker setup command and add a sealed secret for `cloudflared-token`.
6. Push image tags to GHCR or change the image tags in:
   - `infra/apps/frontend/deployment.yaml`
   - `infra/apps/backend/deployment.yaml`

## Bootstrap

Run this on the VPS from a clone of the repo:

```sh
sudo ./scripts/bootstrap-k3s-argocd.sh
```

The script installs k3s when it is not already active, disables bundled Traefik and ServiceLB, installs Argo CD, and applies the root app.

## Private Argo CD UI

```sh
sudo KUBECONFIG=/etc/rancher/k3s/k3s.yaml kubectl -n argocd port-forward svc/argocd-server 8080:443
```

Initial admin password:

```sh
sudo KUBECONFIG=/etc/rancher/k3s/k3s.yaml kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d
```

## Checks

```sh
sudo KUBECONFIG=/etc/rancher/k3s/k3s.yaml kubectl get nodes
sudo KUBECONFIG=/etc/rancher/k3s/k3s.yaml kubectl -n argocd get applications
sudo KUBECONFIG=/etc/rancher/k3s/k3s.yaml kubectl -n app get pods
sudo KUBECONFIG=/etc/rancher/k3s/k3s.yaml kubectl -n auth get pods
sudo KUBECONFIG=/etc/rancher/k3s/k3s.yaml kubectl -n cloudflare logs deploy/cloudflared
```
