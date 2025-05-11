# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY backend ./backend
COPY pom.xml .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/RestService-*.jar app.jar

ENV PORT=10000
EXPOSE $PORT
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:$PORT/actuator/health || exit 1

CMD ["java", "-jar", "app.jar", "--server.port=${PORT}"]