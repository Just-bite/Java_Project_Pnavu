# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# 1. Копируем ВСЮ папку backend (с сохранением структуры)
COPY backend ./backend
COPY pom.xml .

# 2. Собираем проект (pom.xml теперь в /app)
RUN mvn clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/RestService-*.jar app.jar

ENV PORT=10000
EXPOSE $PORT
CMD ["java", "-jar", "app.jar", "--server.port=${PORT}"]