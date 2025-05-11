# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY backend ./backend
COPY pom.xml .

RUN mvn clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/RestService-*.jar app.jar

# Render задаёт переменную PORT автоматически
EXPOSE 10000  # Можно указать любой порт для информации, но Render использует PORT переменную

# Используем shell-форму, чтобы переменная $PORT подставлялась правильно
CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]
