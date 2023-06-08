FROM sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.5_8_1.9.0_3.3.0 AS builder

WORKDIR /app
COPY . /app/
RUN sbt package

FROM eclipse-temurin:17.0.5_8-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/scala-3.3.0/scala-microservice-scaffold-assembly-0.1.0-SNAPSHOT.jar /app/microservice.jar

CMD ["java", "-jar", "microservice.jar"]
