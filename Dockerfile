FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build --chown=10001:0 /workspace/target/tennis-racquet-cbr-1.0.0.jar /app/app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]