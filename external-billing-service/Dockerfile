# build stage (JDK + gradle) -> jar 파일 생성
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies

COPY src src
RUN ./gradlew bootJar

# rutime stage (jre) -> jar
# jre는 jdk보다 가볍지만 jar파일을 실행하는데 필요한 환경은 제공한다.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]