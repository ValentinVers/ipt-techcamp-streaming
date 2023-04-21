package ch.ipt.kafka.exercise8.joinaggregate;

import ch.ipt.kafka.techbier.Account;
import ch.ipt.kafka.techbier.AccountBalance;
import ch.ipt.kafka.techbier.AccountPayment;
import ch.ipt.kafka.techbier.Payment;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static ch.ipt.kafka.config.KafkaStreamsDefaultTopology.EXERCISE_8_TOPIC;


//@Component
public class KafkaStreamsJoinAggregate {

    @Value("${source-topic-transactions}")
    private String sourceTransactions;
    @Value("${source-topic-accounts}")
    private String sourceAccounts;

    @Value("${INITIALS}")
    private String initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsJoinAggregate.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        String sinkTopic = EXERCISE_8_TOPIC + initial;

        //Compute the total of all payments for every single customer and create a new schema containing the account information plus the sum of the transactions
        KStream<String, Payment> transactionStream = streamsBuilder.stream(sourceTransactions);
        KStream<String, Account> accountStream = streamsBuilder.stream(sourceAccounts);

        //TODO

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

    private AccountBalance updateBalance(AccountPayment payment, AccountBalance current) {
        current.setAccountId(payment.getAccountId());
        current.setSurname(payment.getSurname());
        current.setLastname(payment.getLastname());
        current.setStreet(payment.getStreet());
        current.setCity(payment.getCity());
        current.setBalance(current.getBalance() + payment.getAmount());

        return current;
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