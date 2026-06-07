#!/usr/bin/env sh
set -eu

REPO_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

if ! command -v kubectl >/dev/null 2>&1; then
  echo "kubectl is required on the VPS before bootstrapping Argo CD." >&2
  exit 1
fi

if ! systemctl is-active --quiet k3s 2>/dev/null; then
  curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="server --disable=traefik --disable=servicelb --write-kubeconfig-mode=600" sh -
fi

export KUBECONFIG="${KUBECONFIG:-/etc/rancher/k3s/k3s.yaml}"

kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -n argocd --server-side --force-conflicts -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl -n argocd rollout status deployment/argocd-server --timeout=180s
kubectl apply -k "${REPO_ROOT}/infra/bootstrap/argocd/root"

cat <<'MSG'
Argo CD bootstrap submitted.

Watch sync:
  kubectl -n argocd get applications

Open the private Argo CD UI:
  kubectl -n argocd port-forward svc/argocd-server 8080:443
MSG
