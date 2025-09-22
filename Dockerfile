### Build Stage
FROM openjdk:21-jdk-slim AS builder
WORKDIR /builder
# Gradle build tool copy
COPY /src/main /builder/src/main
COPY build.gradle settings.gradle gradlew gradlew.bat /builder/
COPY gradle/wrapper/gradle-wrapper.jar /builder/gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.properties /builder/gradle/wrapper/

# Gradle build
RUN chmod +x gradlew
RUN ./gradlew build



### RUN Stage
FROM openjdk:21-jdk-slim AS run
WORKDIR /workspace
# Build stage copy
COPY --from=builder /builder/build/libs/*.jar modusplant-backend.jar

# OTel Java Agent 원격 다운로드
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# SpringBoot/DataBase ENV (env/dev)
ENV SPRING_PROFILES_ACTIVE=env
ENV TZ="Asia/Seoul"
EXPOSE 80
LABEL maintainer="kodh10@gmail.com"

#Docker 컨테이너 실행 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "modusplant-backend.jar"]