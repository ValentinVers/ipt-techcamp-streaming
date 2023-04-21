package ch.ipt.kafka.config;

import com.azure.data.schemaregistry.SchemaRegistryClient;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroDeserializer;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

import static ch.ipt.kafka.config.KafkaDefault.addTokenCreds;

public class SpecificAvroSerde <T extends org.apache.avro.specific.SpecificRecord>
        implements Serde<Object> {

    private final Serde<Object> inner;

    public SpecificAvroSerde() {
        inner = Serdes.serdeFrom(new KafkaAvroSerializer(), new KafkaAvroDeserializer());
    }

    /**
     * For testing purposes only.
     */
    public SpecificAvroSerde(final SchemaRegistryClient client) {
        if (client == null) {
            throw new IllegalArgumentException("schema registry client must not be null");
        }
        inner = Serdes.serdeFrom(
                new KafkaAvroSerializer(),
                new KafkaAvroDeserializer());
    }

    @Override
    public Serializer<Object> serializer() {
        return inner.serializer();
    }

    @Override
    public Deserializer<Object> deserializer() {
        return inner.deserializer();
    }

    @Override
    public void configure(final Map<String, ?> serdeConfig, final boolean isSerdeForRecordKeys) {
        addTokenCreds((Map<String, Object>) serdeConfig);
        inner.serializer().configure(serdeConfig, isSerdeForRecordKeys);
        inner.deserializer().configure(serdeConfig, isSerdeForRecordKeys);
    }

    @Override
    public void close() {
        inner.serializer().close();
        inner.deserializer().close();
    }
}
