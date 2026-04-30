FROM maven:3.9.15-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean compile package -DskipTests

FROM eclipse-temurin:25-jre-alpine-3.22
WORKDIR /app

COPY --from=build /app/target/khelmel.jar app.jar

ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]