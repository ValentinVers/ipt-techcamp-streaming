# Deployment
## Setup
- Container Registry with Admin enabled
- Container App

## Build and Push
docker image build -t docker-java-jar:latest . ; docker run docker-java-jar:latest
docker tag docker-java-jar:latest techcampstreaming.azurecr.io/docker-java-jar
### Get Credentials for registry
Get credentials from admin user on Azure Portal -> Container Registry -> Access Keys

docker login 

docker push techcampstreaming.azurecr.io/docker-java-jar

## Cross Platform build with Mac M1
docker buildx build --platform linux/amd64 --push -t techcampstreaming.azurecr.io/docker-java-amd64 .

# Umgebung konfigurieren

Diese App produziert, konsumiert und verarbeitet Kafka Events. 

Es gibt 2 Umgebuingen welche konfigurierte werden können:

* Confluent Cloud
* Lokaler Redpanda Cluster

## Confluent Cloud

1. Aktives Spring Boot Profile auf `confluent` setzen: [Anleitung IntelliJ](https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html#modify-options)

2. In `src/main/resources/application-confluent.yml` folgende Werte befüllen.
   * INITIALS (z.B. `PGR`)
   * **Kafka Config:**
     * KAFKA_KEY
     * KAFKA_SECRET
   
   * **Schema Registry Config:**
     * SR_API_KEY
     * SR_API_SECRET

   (Kafka & Schema Registry Config werden dir von CSU und DKN zur Verfügung gestellt.)

3. Falls persönlicher Kafka Cluster verwendet wird:

   Kafka Producer aktivieren
   
   In Klasse `ch.ipt.kafka.producer.PaymentProducer` & `ch.ipt.kafka.producer.AccountProducer` die `@Configuration` Annotation aktivieren.


## Lokaler Redpanda Cluster

1. Redpanda starten (siehe unten)
2. Redpanda Profil konfigurieren

   In `src/main/resources/application-redpanda.yml` folgende Werte befüllen.
   * INITIALS (z.B. `PGR-redpanda`)  

3. Redpanda Profil aktivieren

   Aktives Spring Boot Profile auf `redpanda` setzen: [Anleitung IntelliJ](https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html#modify-options)

4. Kafka Producer aktivieren

   In Klasse `ch.ipt.kafka.producer.PaymentProducer` & `ch.ipt.kafka.producer.AccountProducer` die `@Configuration` Annotation aktivieren.


### Start Redpanda
```
cd src/main/resources/redpanda
docker-compose up -d
docker-compose logs -f
```

### Redpanda UI
Auf http://localhost:8080 findest du alle Informationen zu deiner Redpanda Instanz. 

### Redpanda zurücksetzen
```
cd src/main/resources/redpanda
docker-compose kill
docker-compose rm
```

## App starten

Die Klasse `ch.ipt.kafka.TechBierKafkaStreamsApplication` starten (Vorher muss korrektes Intellij Profil gesetzt werden)

Du solltest jetzt in den Topics `accounts`& `transactions` messages sehen. Du kannst jetzt auf diese reagieren.

## App erweitern

Aktivere die jeweiligen Klassen in `exerciseX.X` dort kannst du einzelne Streaming Applikationen erstellen.




