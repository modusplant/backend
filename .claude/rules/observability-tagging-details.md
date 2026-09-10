---
paths:
  - "src/main/resources/logback-spring.xml"
  - "src/main/resources/application*.yml"
  - "docker-compose*.yml"
---

# Observability Tagging Conventions

Applies to the identity and correlation tags the backend emits on traces, logs, and metrics, and
to how the monitoring stack (OpenTelemetry Collector, Tempo, Loki via Promtail, Prometheus,
Grafana) carries the same values so the three signals join.

---

## 1. Canonical Identity Vocabulary

One value per concept, identical across every signal.

| Concept           | OTel resource attribute (traces) | Log field / Loki label / Prometheus label                    | Value                     |
|-------------------|----------------------------------|--------------------------------------------------------------|---------------------------|
| service name      | `service.name`                   | `service_name`                                               | `modusplant`              |
| service namespace | `service.namespace`              | `service_namespace`                                          | `modusplant`              |
| environment       | `deployment.environment.name`    | `deployment_environment`                                     | `local`, `dev`, or `prod` |
| instance          | `service.instance.id`            | `instance` (Prometheus target)                               | `modusplant-be`           |

`job` (Prometheus scrape job, Promtail stream label) carries the value `modusplant` for the backend, matching the service name.

---

## 2. Correlation Fields

Request-scoped. They live in the log line body as `logfmt` tokens and are never promoted to Loki stream labels.

| Field      | Origin                                                           | Carried by                                                                                                                                                     |
|------------|------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `trace_id` | MDC key `traceId`, populated by `micrometer-tracing-bridge-otel` | log JSON field `trace_id`; Promtail rewrites the line body with `trace_id=<hex>`; Grafana Loki derived field and Tempo `tracesToLogsV2` query parse that token |
| `span_id`  | MDC key `spanId`, populated by `micrometer-tracing-bridge-otel`  | log JSON field `span_id`; Promtail rewrites the line body with `span_id=<hex>`; Grafana Loki derived field                                                     |

---

## 3. Naming Rule

- OTel resource attributes use the dotted `semconv` form (`service.name`, `deployment.environment.name`).
- Every projection into a log field, Loki stream label, or metric label uses `snake_case` (`service_name`, `deployment_environment`, `trace_id`, `span_id`).
- Identifiers are spelled out in full; abbreviated forms such as `env` or `svc` are not used.

---

## 4. Single Source Per Value

| Value                                          | Authoritative source                           | How it reaches the other places                                                                                                                                                                                                      |
|------------------------------------------------|------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `service.name` / `service_name` (`modusplant`) | `spring.application.name` in `application.yml` | Spring Boot derives the OTel `service.name`; `management.metrics.tags.service_name` references it; `logback-spring.xml` reads it through a `springProperty`. The monitoring-stack configs repeat the literal `modusplant`.           |
| `deployment_environment`                       | `application-*.yml` literal                    | `application-*.yml` each set `app.deployment-environment` to `local`, `dev`, or `prod`; `application.yml` references it for the OTel resource attribute and the Micrometer tag, and `logback-spring.xml` reads it for the log field. |
| `service.instance.id` (`modusplant-be`)        | `application.yml` literal                      | Set directly on `management.opentelemetry.resource-attributes.service.instance.id` (traces only). The single-container topology gives it one constant value across every environment.                                                |
| `trace_id` / `span_id`                         | Micrometer tracing MDC                         | `logback-spring.xml` JSON pattern → Promtail pipeline → Grafana.                                                                                                                                                                     |

---

## 5. Backend Emission Points

- `application.yml` → `management.opentelemetry.resource-attributes` supplies `service.namespace`, `deployment.environment.name`, `service.instance.id`; `management.otlp.tracing.endpoint` and `management.otlp.tracing.transport` point the trace exporter at the OpenTelemetry Collector.
- `management.metrics.tags` supplies `service_name` and `deployment_environment` as Micrometer common tags on every meter.
- `application-*.yml` → each sets `app.deployment-environment`, the single source of the environment value.
- `logback-spring.xml` → the `FILE_JSON` encoder writes `service_name`, `deployment_environment`, `trace_id`, `span_id` alongside `message`, `timestamp`, `thread`, `level`, `logger`.