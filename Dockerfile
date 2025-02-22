# Use a Maven image with OpenJDK 21 for building the project
FROM maven:3.9.8 AS build
# Set JAVA_OPTS environment variable

ENV JAVA_OPTS="-XX:+EnableDynamicAgentLoading"

# Copy application source and other necessary files
COPY src /app/src
COPY .env /app/.env
COPY pom.xml /app

# Set the working directory to /app
WORKDIR /app

# Run Maven build with the specified JAVA_OPTS
RUN mvn clean install -U

#FROM openjdk:21
#WORKDIR /app
#COPY .env /app/.env
#COPY --from=build app/target/expense-tracker-0.0.1-SNAPSHOT.jar /app/app.jar
#
#EXPOSE 8081
#
#CMD ["java", "-jar", "app.jar"]