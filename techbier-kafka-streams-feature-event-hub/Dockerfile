FROM openjdk:11
COPY target/techbier-kafka-streams-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application-eventhub.yaml application-eventhub.yaml
ENTRYPOINT ["java","-Dspring.profiles.active=eventhub","-jar","/app.jar"]