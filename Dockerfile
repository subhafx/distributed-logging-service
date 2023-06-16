FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /workspace/app/target .

EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","logger-service.jar"]