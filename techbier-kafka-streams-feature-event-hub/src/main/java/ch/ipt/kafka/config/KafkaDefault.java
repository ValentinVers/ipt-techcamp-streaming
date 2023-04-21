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
    private static final TokenCredential TOKEN_CREDENTIALS = new DefaultAzureCredentialBuilder().build();

    public KafkaDefault(KafkaProperties properties) {
        this.properties = properties;
    }


    @Bean
    public KafkaProducer<String, Object> kafkaProducer() {
        Map<String, Object> props = properties.getProducer().buildProperties();
        addTokenCreds(props);
        return new KafkaProducer<String, Object>(props);
    }


    public static void addTokenCreds(Map<String, Object> props) {
        props.put("specific.avro.reader", true);
        props.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS_CONFIG, true);
        props.put("schema.registry.credential", TOKEN_CREDENTIALS);
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactoryObjectObject() {
        Map<String, Object> props = properties.getConsumer().buildProperties();
        addTokenCreds(props);
        return new DefaultKafkaConsumerFactory<>(props);
    }


}