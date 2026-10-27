FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build --chown=10001:0 /workspace/target/tennis-racquet-cbr-1.0.0.jar /app/app.jar
RUN mkdir /app/data && chown 10001:0 /app/data
USER 10001
VOLUME /app/data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]