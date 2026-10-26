---
paths:
  - "Dockerfile"
  - ".dockerignore"
  - "docker-compose.yml"
  - "docker-compose.override.yml"
---

# Local Docker Environment Details

Applies to @Dockerfile, @.dockerignore, @docker-compose.yml, and @docker-compose.override.yml.

---

## 1. Files

- @Dockerfile: Runtime-only image; copies a prebuilt jar and runs it. No source compilation happens inside Docker.
- @.dockerignore: Limits the build context to the prebuilt jar at `build/docker/modusplant-backend.jar`.
- @build.gradle (`assembleDocker` task): Copies the `bootJar` output to `build/docker/modusplant-backend.jar`.
- @docker-compose.yml: Base `backend` service.
- @docker-compose.override.yml: Local layer auto-merged on top of `docker-compose.yml`.

## 2. Prerequisites

- The external `nginx_proxy` network exists (`docker network create nginx_proxy`, once).
- Every Compose file listed under `include` in `docker-compose.override.yml` exists on the host.

## 3. Workflow

1. `./gradlew assembleDocker --no-daemon` — produce the jar in the build context.
2. `docker compose build` — build the image from the jar.
3. `docker compose up -d` — start the backend together with the included stacks.
