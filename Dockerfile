# --- 1단계: 애플리케이션 빌드 스테이지 ---
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradle ./gradle

COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew bootJar --no-daemon


# --- 2단계: 최종 이미지 생성 스테이지 ---
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx1024m"

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
