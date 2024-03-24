//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.rabbitmq.sink;

import io.confluent.connect.rabbitmq.common.RabbitMQConnectorConfig;
import io.confluent.connect.utils.ConfigKeys;
import io.confluent.connect.utils.recommenders.Recommenders;
import io.confluent.connect.utils.validators.Validators;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;

public class RabbitMQSinkConnectorConfig extends RabbitMQConnectorConfig {
    private static final String RABBITMQ_GROUP = "RabbitMQ";
    public static final String DESTINATION_EXCHANGE_CONFIG = "rabbitmq.exchange";
    private static final String DESTINATION_EXCHANGE_DISPLAY = "RabbitMQ Destination Exchange";
    private static final String DESTINATION_EXCHANGE_DOC = "The destination RabbitMQ exchange where messages need to be delivered. The connector will deliver messages to this one RabbitMQ exchange even when the connector consumes from multiple specified Kafka topics.";
    private static final Object DESTINATION_EXCHANGE_DEFAULT;
    public static final String MESSAGE_ROUTING_KEY_CONFIG = "rabbitmq.routing.key";
    private static final String MESSAGE_ROUTING_KEY_DISPLAY = "RabbitMQ Message Routing Key";
    private static final String MESSAGE_ROUTING_KEY_DOC = "RabbitMQ routing key that dictates how the message travels once it reaches RabbitMQ.";
    private static final Object MESSAGE_ROUTING_KEY_DEFAULT;
    public static final String DELIVERY_MODE_CONFIG = "rabbitmq.delivery.mode";
    private static final String DELIVERY_MODE_DISPLAY = "RabbitMQ Message Delivery Mode";
    private static final String DELIVERY_MODE_DOC = "PERSISTENT or TRANSIENT, decides message durability in RabbitMQ.";
    private static final Object DELIVERY_MODE_DEFAULT;
    public static final String FORWARD_KAFKA_KEY_CONFIG = "rabbitmq.forward.kafka.key";
    private static final String FORWARD_KAFKA_KEY_DISPLAY = "Forward Kafka Record Key";
    private static final String FORWARD_KAFKA_KEY_DOC = "If enabled, the Kafka record key is converted to a string and forwarded on the correlationID property of the RabbitMQ Message. In case the Kafka record key is null and this value is true, no correlationID will be sent.";
    private static final boolean FORWARD_KAFKA_KEY_DEFAULT = false;
    public static final String FORWARD_KAFKA_METADATA_CONFIG = "rabbitmq.forward.kafka.metadata";
    private static final String FORWARD_KAFKA_METADATA_DISPLAY = "Forward Kafka Record Metadata";
    private static final String FORWARD_KAFKA_METADATA_DOC = "If enabled, metadata from the Kafka record is forwarded on the RabbitMQ Message as headers. This includes the record's topic, partition, and offset. The topic name is applied as a header named KAFKA_TOPIC, the partition value is applied as a header named KAFKA_PARTITION, and the offset value is applied as a header named KAFKA_OFFSET.";
    private static final boolean FORWARD_KAFKA_METADATA_DEFAULT = false;
    public static final String FORWARD_KAFKA_HEADERS_CONFIG = "rabbitmq.forward.kafka.headers";
    private static final String FORWARD_KAFKA_HEADERS_DISPLAY = "Forward Kafka Record Headers";
    private static final String FORWARD_KAFKA_HEADERS_DOC = "If enabled, Kafka record headers are added to the RabbitMQ Message as headers.";
    private static final boolean FORWARD_KAFKA_HEADERS_DEFAULT = false;
    private static final String RABBITMQ_PUBLISH_GROUP = "RabbitMQ Publishing";
    public static final String PUBLISH_MAX_BATCH_SIZE_CONFIG = "rabbitmq.publish.max.batch.size";
    private static final String PUBLISH_MAX_BATCH_SIZE_DISPLAY = "Maximum batch size for publish acknowledgements";
    private static final String PUBLISH_MAX_BATCH_SIZE_DOC = "Maximum number of messages in a batch to block on for acknowledgements.";
    private static final int PUBLISH_MAX_BATCH_SIZE_DEFAULT = 100;
    public static final String PUBLISH_TIMEOUT_CONFIG = "rabbitmq.publish.ack.timeout";
    private static final String PUBLISH_TIMEOUT_DISPLAY = "Time to wait for message acknowledgements";
    private static final String PUBLISH_TIMEOUT_DOC = "Period of time to wait for message acknowledgement in milliseconds.";
    private static final Object PUBLISH_TIMEOUT_DEFAULT;
    public static final String PUBLISH_RETRIES_CONFIG = "rabbitmq.publish.max.retries";
    private static final String PUBLISH_RETRIES_DISPLAY = "Message publish retries";
    private static final String PUBLISH_RETRIES_DOC = "Number of retries for un-acked or n-acked messages.";
    private static final int PUBLISH_RETRIES_DEFAULT = 1;

    public RabbitMQSinkConnectorConfig(Map<?, ?> originals) {
        super(config(), originals);
    }

    public static ConfigDef config() {
        ConfigDef configDef = RabbitMQConnectorConfig.config();
        defineBaseConfigKeys().addKeysTo(configDef, Collections.emptyMap());
        definePublishConfigKeys().addKeysTo(configDef, Collections.emptyMap());
        return configDef;
    }

    private static ConfigKeys defineBaseConfigKeys() {
        ConfigKeys configKeys = new ConfigKeys();
        configKeys.define("rabbitmq.exchange").defaultValue(DESTINATION_EXCHANGE_DEFAULT).displayName("RabbitMQ Destination Exchange").documentation("The destination RabbitMQ exchange where messages need to be delivered. The connector will deliver messages to this one RabbitMQ exchange even when the connector consumes from multiple specified Kafka topics.").type(Type.STRING).importance(Importance.HIGH).width(Width.LONG).group("RabbitMQ");
        configKeys.define("rabbitmq.routing.key").defaultValue(MESSAGE_ROUTING_KEY_DEFAULT).displayName("RabbitMQ Message Routing Key").documentation("RabbitMQ routing key that dictates how the message travels once it reaches RabbitMQ.").type(Type.STRING).importance(Importance.HIGH).width(Width.MEDIUM).group("RabbitMQ");
        configKeys.define("rabbitmq.delivery.mode").defaultValue(DELIVERY_MODE_DEFAULT).displayName("RabbitMQ Message Delivery Mode").documentation("PERSISTENT or TRANSIENT, decides message durability in RabbitMQ.").type(Type.STRING).validator(Validators.oneStringOf(Arrays.asList(DeliveryMode.class.getEnumConstants()), false, Enum::name)).recommender(Recommenders.enumValues(DeliveryMode.class, new DeliveryMode[0])).importance(Importance.HIGH).width(Width.MEDIUM).group("RabbitMQ");
        configKeys.define("rabbitmq.forward.kafka.key").defaultValue(false).displayName("Forward Kafka Record Key").documentation("If enabled, the Kafka record key is converted to a string and forwarded on the correlationID property of the RabbitMQ Message. In case the Kafka record key is null and this value is true, no correlationID will be sent.").type(Type.BOOLEAN).importance(Importance.LOW).width(Width.MEDIUM).group("RabbitMQ");
        configKeys.define("rabbitmq.forward.kafka.metadata").defaultValue(false).displayName("Forward Kafka Record Metadata").documentation("If enabled, metadata from the Kafka record is forwarded on the RabbitMQ Message as headers. This includes the record's topic, partition, and offset. The topic name is applied as a header named KAFKA_TOPIC, the partition value is applied as a header named KAFKA_PARTITION, and the offset value is applied as a header named KAFKA_OFFSET.").type(Type.BOOLEAN).importance(Importance.LOW).width(Width.MEDIUM).group("RabbitMQ");
        configKeys.define("rabbitmq.forward.kafka.headers").defaultValue(false).displayName("Forward Kafka Record Headers").documentation("If enabled, Kafka record headers are added to the RabbitMQ Message as headers.").type(Type.BOOLEAN).importance(Importance.LOW).width(Width.MEDIUM).group("RabbitMQ");
        return configKeys;
    }

    private static ConfigKeys definePublishConfigKeys() {
        ConfigKeys configKeys = new ConfigKeys();
        configKeys.define("rabbitmq.publish.max.batch.size").defaultValue(100).displayName("Maximum batch size for publish acknowledgements").documentation("Maximum number of messages in a batch to block on for acknowledgements.").type(Type.INT).validator(Validators.atLeast(0)).importance(Importance.MEDIUM).width(Width.MEDIUM).group("RabbitMQ Publishing");
        configKeys.define("rabbitmq.publish.ack.timeout").defaultValue(PUBLISH_TIMEOUT_DEFAULT).displayName("Time to wait for message acknowledgements").documentation("Period of time to wait for message acknowledgement in milliseconds.").type(Type.INT).validator(Validators.atLeast(0)).importance(Importance.MEDIUM).width(Width.MEDIUM).group("RabbitMQ Publishing");
        configKeys.define("rabbitmq.publish.max.retries").defaultValue(1).displayName("Message publish retries").documentation("Number of retries for un-acked or n-acked messages.").type(Type.INT).validator(Validators.atLeast(0)).importance(Importance.MEDIUM).width(Width.MEDIUM).group("RabbitMQ Publishing");
        return configKeys;
    }

    public String exchange() {
        return this.getString("rabbitmq.exchange");
    }

    public String routingKey() {
        return this.getString("rabbitmq.routing.key");
    }

    public DeliveryMode deliveryMode() {
        String deliveryMode = this.getString("rabbitmq.delivery.mode").toUpperCase();
        return DeliveryMode.valueOf(deliveryMode);
    }

    public boolean forwardKafkaKey() {
        return this.getBoolean("rabbitmq.forward.kafka.key");
    }

    public boolean forwardKafkaMetadata() {
        return this.getBoolean("rabbitmq.forward.kafka.metadata");
    }

    public boolean forwardKafkaHeaders() {
        return this.getBoolean("rabbitmq.forward.kafka.headers");
    }

    public int maxBatchSize() {
        return this.getInt("rabbitmq.publish.max.batch.size");
    }

    public int timeout() {
        return this.getInt("rabbitmq.publish.ack.timeout");
    }

    public int retries() {
        return this.getInt("rabbitmq.publish.max.retries");
    }

    public static void main(String[] args) {
        System.out.println(config().toEnrichedRst());
    }

    static {
        DESTINATION_EXCHANGE_DEFAULT = ConfigDef.NO_DEFAULT_VALUE;
        MESSAGE_ROUTING_KEY_DEFAULT = ConfigDef.NO_DEFAULT_VALUE;
        DELIVERY_MODE_DEFAULT = ConfigDef.NO_DEFAULT_VALUE;
        PUBLISH_TIMEOUT_DEFAULT = 10000;
    }
}
