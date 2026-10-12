FROM eclipse-temurin:21-jre
WORKDIR /workspace

# assembleDocker Task로부터 생성된 JAR를 이미지에 복사
COPY build/docker/modusplant-backend.jar modusplant-backend.jar

ENV TZ="Asia/Seoul"
EXPOSE 8080
LABEL maintainer="kodh10@gmail.com"

ENTRYPOINT ["java", "-jar", "modusplant-backend.jar"]