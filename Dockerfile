FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY backend ./backend
COPY pom.xml .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/RestService-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]