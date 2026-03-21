FROM maven:3.9-eclipse-temurin-17
WORKDIR /app
COPY pom.xml .
COPY src ./src
# We compile and run the app directly using Maven
EXPOSE 9000
ENTRYPOINT ["mvn", "spring-boot:run"]