---
name: configure-docker-environment
description: Guides the high-level architecture of how Docker builds and deploys the Spring Boot backend across environments — local, dev, and prod.
---

# Docker Environment Configuration

## Overview

Two independent build paths drive three environments:
- **Local**: driven entirely by the root-level Compose files (`docker-compose.yml`, `docker-compose.override.yml`, `Dockerfile`, `.dockerignore`).
- **Dev / Prod**: driven entirely by the CI/CD GitHub Actions workflow, which builds and runs the image directly. Local Compose files are not involved.

## Local Environment

- The build context ships only a prebuilt jar, never source — `.dockerignore` excludes everything else. Run `./gradlew assembleDocker` first to copy the `bootJar` output to `build/docker/modusplant-backend.jar` (the same artifact path the CI/CD pipeline produces) before `docker compose build`; the task only copies files, so it works whether or not Docker is installed.
- A local-only `.env` file at the repo root (gitignored, never committed) supplies `JDBC_CONNECTION_URL`/`JDBC_USERNAME`/`JDBC_PASSWORD`, which `docker compose` auto-loads into `docker-compose.yml`'s `backend.build.args`.
- `docker-compose.override.yml` auto-merges on top of `docker-compose.yml` (standard Compose override convention), switching to a local-friendly Spring profile and enabling the container to reach services on the host machine.
- The container joins an external `nginx_proxy` Docker network rather than publishing a host port; that network must be created once before first use.
- Claude Code's own Docker permissions in this repo are tiered: read-only/diagnostic subcommands are auto-allowed, `exec`/`run`/`compose exec` require user confirmation, and destructive subcommands (`rm`, `rmi`, `compose up`, `compose down`) are denied — ask the user to run those manually.

## Dev / Prod Environment (CI/CD Pipeline)

Both environments are driven by a single GitHub Actions workflow.

- **Trigger**: a push to the `develop` branch deploys to dev; pushing a `v*` tag deploys to prod (and cuts a GitHub release); a manual dispatch is also available.
- **Build**: a shared job builds the jar and pushes a tagged image to `ghcr.io/modusplant/backend` — dev uses a `:latest`-style tag, prod uses a tag pinned to the git SHA/tag.
- **Deploy**: separate jobs SSH into the dev host (a home-server Mac mini) or the prod host (AWS EC2), pull the appropriate image, and (re)start the backend container. Prod regenerates its runtime secrets from AWS SSM on every deploy; dev reuses a secrets file already present on the host.

## Environment Comparison

|               | Local               | Dev                  | Prod                        |
|---------------|---------------------|----------------------|-----------------------------|
| Driven by     | Compose files       | CI/CD workflow       | CI/CD workflow              |
| Image tag     | built locally       | `:latest`            | `:<git-sha>` / `:<git-tag>` |
| Deploy target | developer's machine | Mac mini home server | AWS EC2                     |
| Trigger       | manual              | push to `develop`    | push of a `v*` tag          |
