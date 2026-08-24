---
paths:
  - "src/main/java/kr/modusplant/infrastructure/monitor/**"
---

> Supplements CLAUDE.md § Architecture — the `infrastructure` package's monitoring/diagnostics concern. Assumes familiarity with the top-level package roles described there.

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
  - Calls `RedisHelper.setString` three times inside a single try/catch: once with no TTL, once with a 10-second TTL, once with a 1-minute TTL, each under a distinct fixed test key.
  - Any exception caught here is swallowed and replaced with a new `RuntimeException()`.

**`MonitorController`** — `@RestController @RequestMapping("/api/monitor") @RequiredArgsConstructor`:
- `GET /api/monitor/monitor-success` → `monitorService.performBusinessLogic(true)`
- `GET /api/monitor/monitor-error` → `monitorService.performBusinessLogic(false)`
- `GET /api/monitor/monitor-error-controller` → throws `RuntimeException()` directly, with no call into `MonitorService`.
- `GET /api/monitor/monitor-redis` → `monitorService.monitorRedisHelper()`

---

## 3. Request Flow

1. A request hits one of the four `GET` endpoints on `MonitorController`.
2. For `monitor-success`, `monitor-error`, and `monitor-redis`, the controller delegates to the matching `MonitorService` method, which executes its fixed success/failure/Redis-exercise logic.
3. `monitor-error-controller` is the one path that skips the service layer entirely — the exception originates directly in the controller method.