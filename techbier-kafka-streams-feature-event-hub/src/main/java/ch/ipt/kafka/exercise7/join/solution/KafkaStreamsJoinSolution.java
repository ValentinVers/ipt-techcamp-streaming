package ch.ipt.kafka.exercise7.join.solution;

import ch.ipt.kafka.techbier.Account;
import ch.ipt.kafka.techbier.AccountPayment;
import ch.ipt.kafka.techbier.Payment;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TableJoined;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static ch.ipt.kafka.config.KafkaStreamsDefaultTopology.EXERCISE_7_TOPIC;


//@Component
public class KafkaStreamsJoinSolution {


    @Value("${source-topic-transactions}")
    private String sourceTransactions;
    @Value("${source-topic-accounts}")
    private String sourceAccounts;

    @Value("${INITIALS}")
    private String initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsJoinSolution.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        String sinkTopic = EXERCISE_7_TOPIC + initial;

        // filter all Payments for the customers with last name "Fischer"
        // Hint look up fk-join

        KStream<String, Payment> transactionStream = streamsBuilder.stream(sourceTransactions);
        KStream<String, Account> accountStream = streamsBuilder.stream(sourceAccounts);

        KTable<String, Account> accountTable = accountStream.toTable(Materialized.as(EXERCISE_7_TOPIC + "account"));
        transactionStream.toTable(Materialized.as(EXERCISE_7_TOPIC + "transaction"))
                .join(accountTable, t -> t.getAccountId().toString(), this::joinAccountTransaction,
                        TableJoined.as(EXERCISE_7_TOPIC + "table-joined"),
                        Materialized.as(EXERCISE_7_TOPIC + "join"))
                .toStream()
                .filter((k, v) -> "Fischer".equals(v.getLastname().toString()))
                .peek((key, value) -> LOGGER.info("Message of filtered join: key={}, value={}", key, value))
                .to(sinkTopic);

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

    private AccountPayment joinAccountTransaction(Payment payment, Account account) {
        return new AccountPayment(account.getAccountId(),
                account.getSurname(),
                account.getLastname(),
                account.getStreet(),
                account.getCity(),
                payment.getId(),
                payment.getCardNumber(),
                payment.getCardType(),
                payment.getAmount());
    }

}