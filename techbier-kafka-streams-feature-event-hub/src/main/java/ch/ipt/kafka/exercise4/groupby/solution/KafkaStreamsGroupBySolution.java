package ch.ipt.kafka.exercise4.groupby.solution;

import ch.ipt.kafka.techbier.Payment;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static ch.ipt.kafka.config.KafkaStreamsDefaultTopology.EXERCISE_4_TOPIC;


//@Component
public class KafkaStreamsGroupBySolution {


    @Value("${source-topic-transactions}")
    private String sourceTopic;

    @Value("${INITIALS}")
    private String initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsGroupBySolution.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        String sinkTopic = EXERCISE_4_TOPIC + initial;

        //count the number of payments grouped by the cardtype (e.g. "Debit": 12, "Credit": 27)

        KStream<String, Payment> stream = streamsBuilder.stream(sourceTopic);

        stream.groupBy((k, v) -> v.getCardType().toString())
                .count(Materialized.as(EXERCISE_4_TOPIC + "count"))
                .toStream()
                .peek((key, value) -> LOGGER.info("Grouped Transactions: key={}, value={}", key, value))
                .to(sinkTopic);

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}
