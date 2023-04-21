package ch.ipt.kafka.config;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;


@Configuration
/*
 * This class is only here to have a basic topology so the project is runnable.
 */
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaDefault {

    private final KafkaProperties properties;
    private final TokenCredential tokenCredential;

    public KafkaDefault(KafkaProperties properties) {
        this.properties = properties;
        tokenCredential = new DefaultAzureCredentialBuilder().build();
    }


    @Bean
    public KafkaProducer<String, Object> kafkaProducer() {
        Map<String, Object> props = properties.getProducer().buildProperties();
        addTokeCreds(props);
        return new KafkaProducer<String, Object>(props);
    }


    private void addTokeCreds(Map<String, Object> props) {
        props.put("specific.avro.reader", true);
        props.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS_CONFIG, true);
        props.put("schema.registry.credential", tokenCredential);
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactoryObjectObject() {
        Map<String, Object> props = properties.getConsumer().buildProperties();
        addTokeCreds(props);
        return new DefaultKafkaConsumerFactory<>(props);
    }


}