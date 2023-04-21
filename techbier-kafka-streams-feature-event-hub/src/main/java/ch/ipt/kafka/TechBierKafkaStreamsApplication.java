package ch.ipt.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableKafkaStreams
public class TechBierKafkaStreamsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechBierKafkaStreamsApplication.class, args);
    }

}
