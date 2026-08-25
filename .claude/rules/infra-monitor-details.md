---
paths:
  - "src/main/java/kr/modusplant/infrastructure/monitor/**"
---

# Monitor Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.monitor`.

---

## 1. Purpose

This package is diagnostic-only — it contains no real business logic. It exists so ops tooling (logging pipelines, APM, alerting) has known-good and known-bad HTTP endpoints to exercise, including a real Redis read/write round-trip.

The package is flat: two classes (`MonitorService`, `MonitorController`), no sub-packages.

---

## 2. Class Responsibilities

**`MonitorService`** — `@Service @RequiredArgsConstructor`:
- `performBusinessLogic(boolean shouldNotThrowError)`:
  - `shouldNotThrowError = true` → returns the success string
  - `shouldNotThrowError = false` → throws `RuntimeException()`
- `monitorRedisHelper()`:
  - Any exception caught here is swallowed and replaced with a new `RuntimeException()`.
- `monitorAmazonS3()`:
  - Call failures (network errors, etc.) are caught and replaced with a new `RuntimeException()`.

**`MonitorController`** — `@RestController @RequestMapping("/api/admin/v1/monitor") @RequiredArgsConstructor`:
- `GET /api/admin/v1/monitor/monitor-success` → `monitorService.performBusinessLogic(true)`
- `GET /api/admin/v1/monitor/monitor-error` → `monitorService.performBusinessLogic(false)`
- `GET /api/admin/v1/monitor/monitor-error-controller` → throws `RuntimeException()` directly, with no call into `MonitorService`.
- `GET /api/admin/v1/monitor/monitor-redis` → `monitorService.monitorRedisHelper()`
- `GET /api/admin/v1/monitor/monitor-amazon-s3` → `monitorService.monitorAmazonS3()`

---

## 3. Request Flow

1. A request hits one of the `GET` endpoints on `MonitorController`.
2. For `monitor-success`, `monitor-error`, `monitor-redis`, and `monitor-amazon-s3`, the controller delegates to the matching `MonitorService` method, which executes its fixed logic.
3. `monitor-error-controller` is the one path that skips the service layer entirely — the exception originates directly in the controller method.