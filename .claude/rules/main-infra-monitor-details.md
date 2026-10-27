---
paths:
  - "src/main/java/kr/modusplant/infrastructure/monitor/**"
---

# Monitor Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.monitor`.

---

## 1. Purpose

This package is diagnostic-only — it contains no real business logic.

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