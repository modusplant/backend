---
name: guide-prod-dev-ci-cd-pipeline-configuration
description: Guides the GitHub Actions CI/CD pipeline that tests, builds, and deploys the Spring Boot backend to the prod and dev environments.
---

# Prod / Dev CI/CD Pipeline Configuration

The pipeline is defined in @.github/workflows/modusplant-backend-ci-cd.yml.

## Triggers

- Push to `develop`: common pipeline, then the dev pipeline.
- Push of a `v*` tag: common pipeline, then the prod pipeline.
- Manual dispatch: common pipeline, then the dev pipeline when dispatched on `develop` or the prod pipeline when dispatched on a `v*` tag; no deploy on any other ref.

## Job Dependency Graph

```
Test → Build and Push Image ─┬→ Deploy to Prod (EC2) → Create GitHub Release
                             └→ Deploy to Dev (Mac mini)
```

## Common Pipeline

### Test

| Step                                         | Description                                                                               |
|----------------------------------------------|-------------------------------------------------------------------------------------------|
| Checkout Source Code                         | Fetch the repository.                                                                     |
| Set up JDK 21                                | Install Temurin JDK 21 with Gradle caching.                                               |
| Configure AWS credentials (for SSM)          | Authenticate to AWS for SSM Parameter Store access.                                       |
| Load dev environment variables from SSM      | Load dev DB, Redis, Wasabi, admin, and Firebase values into the environment.              |
| Print DB connection environment variables    | Log the DB URL and username, and whether the password is set.                             |
| Grant execute permission to gradlew          | Make the Gradle wrapper executable.                                                       |
| Run Gradle tests                             | Run the test suite against dev resources, with dummy values for OAuth and mail providers. |

### Build and Push Image

| Step                                                  | Description                                                                                                                                 |
|-------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| Checkout Source Code                                  | Fetch the repository.                                                                                                                       |
| Configure AWS credentials (for SSM)                   | Authenticate to AWS for SSM Parameter Store access.                                                                                         |
| Load GHCR token and dev DB credentials from SSM       | Load the GHCR access token and the dev DB credentials.                                                                                      |
| Set up JDK 21                                         | Install Temurin JDK 21 with Gradle caching.                                                                                                 |
| Grant execute permission to gradlew                   | Make the Gradle wrapper executable.                                                                                                         |
| Migrate dev DB, generate jOOQ code, and build bootJar | Apply Flyway migrations to the dev DB, generate jOOQ classes from it, build the jar, and place it at `build/docker/modusplant-backend.jar`. |
| Set up Docker Buildx                                  | Enable multi-platform image builds.                                                                                                         |
| Log in to GitHub Container Registry                   | Authenticate to `ghcr.io`.                                                                                                                  |
| Resolve Docker image tags                             | Tag with `latest` and the commit SHA, plus the git tag on tag pushes.                                                                       |
| Build and push ARM64 image to GHCR                    | Build a `linux/arm64` image to `ghcr.io/modusplant/backend` and push all tags.                                                              |

## Prod Pipeline

### Deploy to Prod (EC2)

| Step                                     | Description                                                                                                                                                                                                                                                       |
|------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Configure AWS credentials (for SSM)      | Authenticate to AWS for SSM Parameter Store access.                                                                                                                                                                                                               |
| Load prod deploy parameters from SSM     | Load the GHCR token, EC2 address and SSH key, and log paths.                                                                                                                                                                                                      |
| Write prod EC2 SSH key to temporary file | Write the SSH key to a `600`-mode file.                                                                                                                                                                                                                           |
| Deploy to prod EC2 via SSH               | On EC2: write prod SSM parameters to `/opt/modusplant/.env.prod.new`, pull the commit-SHA image, and swap the `modusplant-be` container with `prod` profile (see Container Swap and Rollback); once healthy, promote the env file to `/opt/modusplant/.env.prod`. |
| Remove temporary SSH key file            | Delete the SSH key file, even on failure.                                                                                                                                                                                                                         |

### Create GitHub Release

| Step                                  | Description                                                                   |
|---------------------------------------|-------------------------------------------------------------------------------|
| Checkout Source Code                  | Fetch the repository.                                                         |
| Create GitHub Release with auto notes | Create a release for the tag with generated notes; skip if it already exists. |

## Dev Pipeline

### Deploy to Dev (Mac mini)

| Step                                       | Description                                                                                                                                                                 |
|--------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Configure AWS credentials (for SSM)        | Authenticate to AWS for SSM Parameter Store access.                                                                                                                         |
| Load dev deploy parameters from SSM        | Load the GHCR token, dev server address and SSH key, and log paths.                                                                                                         |
| Write dev server SSH key to temporary file | Write the SSH key to a `600`-mode file.                                                                                                                                     |
| Deploy to dev Mac mini via SSH             | On the home server: pull the `latest` image and swap the `modusplant-be` container with `dev` profile using the host-resident `.env.dev` (see Container Swap and Rollback). |
| Remove temporary SSH key file              | Delete the SSH key file, even on failure.                                                                                                                                   |

## Container Swap and Rollback

Both deploy steps swap the `modusplant-be` container as follows:

1. Remove any `modusplant-be-previous` left by an interrupted deploy.
2. If `modusplant-be` exists, stop it and rename it to `modusplant-be-previous`.
3. Start the new `modusplant-be` and wait until its readiness health check reports healthy.
4. On success, remove `modusplant-be-previous`, then remove the `ghcr.io/modusplant/backend` images that no container uses, along with all dangling images.
5. On failure (`docker run` error, `unhealthy` status, or a container restart), print the new container's last 100 log lines, remove it, rename `modusplant-be-previous` (if present) back to `modusplant-be`, start it, and fail the job. Prod also deletes `/opt/modusplant/.env.prod.new`.