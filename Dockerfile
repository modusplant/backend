FROM eclipse-temurin:21-jre
WORKDIR /workspace

# ffmpeg 라이브러리 install
RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# assembleDocker Task로부터 생성된 JAR를 이미지에 복사
COPY build/docker/modusplant-backend.jar modusplant-backend.jar

ENV TZ="Asia/Seoul"
EXPOSE 8080
LABEL maintainer="kodh10@gmail.com"

ENTRYPOINT ["java", "-jar", "modusplant-backend.jar"]