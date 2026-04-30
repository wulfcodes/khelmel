# Build Stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/khelmel-1.0-SNAPSHOT-jar-with-dependencies.jar ./app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]