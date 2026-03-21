FROM maven:3.9-eclipse-temurin-17
WORKDIR /app
COPY pom.xml .
COPY src ./src
EXPOSE 8001
ENTRYPOINT ["mvn", "spring-boot:run"]