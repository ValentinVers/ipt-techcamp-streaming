package ch.ipt.kafka.config;

import ch.ipt.kafka.techbier.Payment;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
/*
 * This class is only here to have a basic topology so the project is runnable.
 */
public class KafkaStreamsDefaultTopology {

    @Value("${source-topic-transactions}")
    private String sourceTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsDefaultTopology.class);

    public static final String EXERCISE_1_TOPIC = "filtered-transactions-";
    public static final String EXERCISE_2_TOPIC = "rounded-transactions-";
    public static final String EXERCISE_3_TOPIC_CREDIT = "credit-transactions-";
    public static final String EXERCISE_3_TOPIC_DEBIT = "debit-transactions-";
    public static final String EXERCISE_3_TOPIC_UNKNOWN = "undefined-transactions-";
    public static final String EXERCISE_4_TOPIC = "grouped-transactions-";
    public static final String EXERCISE_5_TOPIC = "total-of-transactions-";
    public static final String EXERCISE_6_TOPIC = "transactions-last-minute-";
    public static final String EXERCISE_7_TOPIC = "filtered-join-";
    public static final String EXERCISE_8_TOPIC = "sum-transactions-per-account-";

    @Value("${INITIALS}")
    private String initial;

    @Value("${topic-replicas}")
    private int replicas;

    @Value("${source-topic-accounts}")
    private String accountTopic;

    @Value("${source-topic-transactions}")
    private String transactionTopic;


//    @Bean
    KStream<String, Payment> buildPipeline(StreamsBuilder streamsBuilder) {

        //default topology which peeks at every transaction

        KStream<String, Payment> messageStream = streamsBuilder.stream(sourceTopic);

        messageStream.peek((key, payment) -> LOGGER.trace("Message: key={}, value={}", key, payment));

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));

        return messageStream;
    }

    @Bean
    public NewTopic createTransactionTopic() {
        return createTopic(transactionTopic);
    }

    @Bean
    public NewTopic createAccountTopic() {
        return createTopic(accountTopic);
    }

    @Bean
    public NewTopic createTopic1() {
        return createTopic(EXERCISE_1_TOPIC + initial);
    }

    @Bean
    public NewTopic createTopic2() {
        return createTopic(EXERCISE_2_TOPIC + initial);
    }

    @Bean
    public NewTopic createTopic3Debit() {
        return createTopic(EXERCISE_3_TOPIC_DEBIT + initial);
    }

    @Bean
    public NewTopic createTopic3Credit() {
        return createTopic(EXERCISE_3_TOPIC_CREDIT + initial);
    }

    @Bean
    public NewTopic createTopic3Unknown() {
        return createTopic(EXERCISE_3_TOPIC_UNKNOWN + initial);
    }

    @Bean
    public NewTopic createTopic4() {
        return createTopic(EXERCISE_4_TOPIC + initial);
    }

    @Bean
    public NewTopic createTopic5() {
        return createTopic(EXERCISE_5_TOPIC + initial);
    }

    @Bean
    public NewTopic createTopic6() {
        return createTopic(EXERCISE_6_TOPIC + initial);
    }

    @Bean
    public NewTopic createTopic7() {
        return createTopic(EXERCISE_7_TOPIC + initial);
    }

    @Bean
    public NewTopic createTopic8() {
        return createTopic(EXERCISE_8_TOPIC + initial);
    }

    private NewTopic createTopic(String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(replicas)
                .build();
    }
}