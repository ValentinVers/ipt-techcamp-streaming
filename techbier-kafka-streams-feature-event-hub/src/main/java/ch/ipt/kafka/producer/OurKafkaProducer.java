package ch.ipt.kafka.producer;

import ch.ipt.kafka.techbier.Account;
import ch.ipt.kafka.techbier.Payment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OurKafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    KafkaProducer<String, Object> kafkaProducerPayment;
    @Autowired
    KafkaProducer<String, Object> kafkaProducerAccount;

    public void sendPayment(Payment message, String topic) {
        kafkaProducerPayment.send(new ProducerRecord<>(topic, message.getId().toString(), message));
//        future.addCallback(new ListenableFutureCallback<>() {
//
//            @Override
//            public void onSuccess(SendResult<String, Payment> result) {
//                LOGGER.info("Message [{}] delivered with offset {}",
//                        message,
//                        result.getRecordMetadata().offset());
//            }
//
//            @Override
//            public void onFailure(Throwable ex) {
//                LOGGER.warn("Unable to deliver message [{}]. {}",
//                        message,
//                        ex.getMessage());
//            }
//        });
    }

    public void sendAccount(Account message, String topic) {
        kafkaProducerAccount.send(new ProducerRecord<>(topic, message.getAccountId().toString(), message));
//
//        future.addCallback(new ListenableFutureCallback<>() {
//
//            @Override
//            public void onSuccess(SendResult<String, Account> result) {
//                LOGGER.info("Message [{}] delivered with offset {}",
//                        message,
//                        result.getRecordMetadata().offset());
//            }
//
//            @Override
//            public void onFailure(Throwable ex) {
//                LOGGER.warn("Unable to deliver message [{}]. {}",
//                        message,
//                        ex.getMessage());
//            }
//        });
//    }
    }
}
