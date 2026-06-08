# Backend Observability

The backend observability baseline is intentionally small: structured logs,
Actuator health checks, and Prometheus-compatible metrics. The goal is to make
runtime failures and slow requests diagnosable without committing to a full
tracing or alerting platform too early.

## Logs

In the `k8s` profile, Spring Boot writes structured JSON logs to stdout using
Elastic Common Schema:

```yaml
logging:
  structured:
    format:
      console: ecs
```

Every HTTP response includes an `X-Request-Id` header. If the request already
has one, the backend preserves it; otherwise it generates one. The value is also
placed in the logging MDC as `request.id` so related log entries can be queried
together by log collection tools.

Log these events at `INFO` when they represent meaningful operational or
business activity:

- Application startup and shutdown.
- Important module-level domain events.
- Security denials without secrets or raw tokens.
- Infrastructure failures involving Postgres, Redis, Flyway, or outbound HTTP.

Do not log routine successful reads at `INFO`. Use metrics for request volume
and latency instead.

## Metrics

Actuator exposes:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`

The Prometheus endpoint is provided by `micrometer-registry-prometheus`.
Spring Boot automatically contributes JVM, HTTP server, datasource, and cache
metrics when the relevant components are present.

Every metric is tagged with:

```yaml
management:
  metrics:
    tags:
      application: magnum-opus
```

## Health Probes

Health probes are enabled through Actuator:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Use these endpoints for Kubernetes probes:

- Liveness: `/actuator/health/liveness`
- Readiness: `/actuator/health/readiness`

When the `k8s` profile sets `server.servlet.context-path: /api`, the externally
visible paths become:

- `/api/actuator/health/liveness`
- `/api/actuator/health/readiness`
- `/api/actuator/prometheus`

## Local Viewing

Run the backend:

```bash
mise run dev:backend
```

Watch logs in the terminal.

Check health:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/liveness
curl http://localhost:8080/actuator/health/readiness
```

`/actuator/health` and `/actuator/health/readiness` can report `DOWN` when a
required dependency such as Redis or Postgres is unavailable. Use liveness to
answer "is the backend process alive?" and readiness to answer "can this
instance serve traffic?"

Inspect metrics:

```bash
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/http.server.requests
curl http://localhost:8080/actuator/prometheus
```

## Kubernetes Viewing

Watch logs:

```bash
kubectl logs deployment/magnum-opus-backend
```

Follow logs:

```bash
kubectl logs -f deployment/magnum-opus-backend
```

Check health through the cluster service or ingress path:

```bash
curl https://<backend-host>/api/actuator/health
```

For the current project scope, start with container stdout logs and Prometheus
scraping. Add Loki, Grafana dashboards, alert rules, and tracing only after the
backend has enough behavior to justify that operational surface.
