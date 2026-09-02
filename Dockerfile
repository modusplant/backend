### Run Stage
FROM eclipse-temurin:21-jre AS run
WORKDIR /workspace

# GitHub Actions/Gradle build step에서 생성된 실행용 jar 복사
COPY build/docker/modusplant-backend.jar modusplant-backend.jar

# SpringBoot/DataBase ENV (secrets/dev)
ENV TZ="Asia/Seoul"
EXPOSE 8080
LABEL maintainer="kodh10@gmail.com"

# Docker 컨테이너 실행 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "modusplant-backend.jar"]
