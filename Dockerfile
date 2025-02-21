#FROM maven:3.9.8-eclipse-temurin-21 AS build
#
#COPY src /app/src
#COPY .env /app/.env
#COPY pom.xml /app
#
#WORKDIR /app
#RUN mvn clean install

FROM openjdk:21
WORKDIR /app
COPY .env /app/.env
COPY  /target/expense-tracker-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]