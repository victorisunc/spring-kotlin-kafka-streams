FROM eclipse-temurin:17.0.2_8-jdk-focal as build
COPY ./springboot-kotlin-kafka-streams-demo-1.0-SNAPSHOT.jar /app.jar
WORKDIR /
CMD ["java", "-jar", "app.jar"]