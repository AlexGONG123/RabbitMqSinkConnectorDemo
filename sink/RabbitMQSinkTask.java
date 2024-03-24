//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.rabbitmq.sink;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.confluent.connect.utils.Version;
import io.confluent.connect.utils.retry.RetryCondition;
import io.confluent.connect.utils.retry.RetryPolicy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.data.Values;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQSinkTask.class);
    RabbitMQSinkConnectorConfig config;
    Connection connection;
    Channel channel;

    public RabbitMQSinkTask() {
    }

    public void start(Map<String, String> settings) {
        this.config = new RabbitMQSinkConnectorConfig(settings);
        ConnectionFactory connectionFactory = this.config.connectionFactory();

        try {
            log.info("Opening connection to RabbitMQ host");
            this.connection = connectionFactory.newConnection();
        } catch (TimeoutException | IOException var5) {
            throw new ConnectException(var5);
        }

        try {
            log.info("Creating Channel");
            this.channel = this.connection.createChannel();
            if (this.channel == null) {
                throw new IOException("No more channels available");
            } else {
                this.channel.confirmSelect();
            }
        } catch (IOException var4) {
            throw new ConnectException(var4);
        }
    }

    public void put(Collection<SinkRecord> records) {
        List<SinkRecord> sinkRecords = new ArrayList(records);
        List<List<SinkRecord>> sinkRecordBatches = new ArrayList();

        for(int i = 0; i < sinkRecords.size(); i += this.config.maxBatchSize()) {
            sinkRecordBatches.add(sinkRecords.subList(i, Math.min(i + this.config.maxBatchSize(), sinkRecords.size())));
        }

        RetryCondition batchRetryCondition = RetryCondition.retryOn(new Class[]{IOException.class, TimeoutException.class});
        RetryPolicy batchRetryPolicy = RetryPolicy.builder().maxRetries(this.config.retries()).when(batchRetryCondition).build();
        log.info("Starting publishing batches to RabbitMQ");
        int counter = 0;
        Iterator var7 = sinkRecordBatches.iterator();

        while(var7.hasNext()) {
            List<SinkRecord> batch = (List)var7.next();
            log.info("Attempting publishing batch {} of {}", counter++, sinkRecordBatches.size());
            Callable<Void> batchPublisher = () -> {
                this.publishBatch(batch);
                return null;
            };
            batchRetryPolicy.call(String.format("publish batch #%d and get acks from RabbitMQ", counter), batchPublisher);
        }

    }

    private void publishBatch(List<SinkRecord> batch) throws InterruptedException, TimeoutException, IOException {
        Iterator var2 = batch.iterator();

        while(var2.hasNext()) {
            SinkRecord record = (SinkRecord)var2.next();
            if (record.value() != null && !(record.value() instanceof byte[])) {
                throw new ConnectException("Error while parsing the record, record value must be of type byte[]");
            }

            try {
                this.channel.basicPublish(this.config.exchange(), this.config.routingKey(), this.getMessageProperties(record), (byte[])((byte[])record.value()));
            } catch (IOException var5) {
                log.error("There was an error while constructing and publishing the outgoing message to RabbitMQ");
                throw new DataException(var5);
            }
        }

        this.channel.waitForConfirmsOrDie((long)this.config.timeout());
    }

    private AMQP.BasicProperties getMessageProperties(SinkRecord record) {
        AMQP.BasicProperties.Builder propertiesBuilder = new AMQP.BasicProperties.Builder();
        propertiesBuilder.deliveryMode(this.config.deliveryMode().getMode());
        if (this.config.forwardKafkaKey()) {
            if (record.key() != null) {
                String convertedKey = Values.convertToString(record.keySchema(), record.key());
                propertiesBuilder.correlationId(convertedKey);
            } else {
                log.debug("Unable to set correlationId because record does not have a key");
            }
        }

        Map<String, Object> headers = new HashMap();
        if (this.config.forwardKafkaMetadata()) {
            headers.put(MessageProperty.KAFKA_TOPIC.name(), record.topic());
            headers.put(MessageProperty.KAFKA_PARTITION.name(), record.kafkaPartition());
            headers.put(MessageProperty.KAFKA_OFFSET.name(), record.kafkaOffset());
        }

        if (this.config.forwardKafkaHeaders()) {
            Iterator var4 = record.headers().iterator();

            while(var4.hasNext()) {
                Header header = (Header)var4.next();
                if (header.value() != null) {
                    String convertedHeader = Values.convertToString(header.schema(), header.value());
                    headers.put("kafka-" + header.key(), convertedHeader);
                }
            }
        }

        return propertiesBuilder.headers(headers).build();
    }

    public Map<TopicPartition, OffsetAndMetadata> preCommit(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        return super.preCommit(currentOffsets);
    }

    public void stop() {
        try {
            this.channel.close();
        } catch (TimeoutException | AlreadyClosedException | IOException var10) {
            log.error("Error thrown while closing channel", var10);
        } finally {
            try {
                this.connection.close();
            } catch (IOException var9) {
                log.error("Error thrown while closing connection", var9);
            }

        }

    }

    public String version() {
        return Version.forClass(this.getClass());
    }
}
