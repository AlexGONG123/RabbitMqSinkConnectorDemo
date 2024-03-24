//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.rabbitmq.common;

import com.github.jcustenborder.kafka.connect.utils.config.ConfigKeyBuilder;
import com.rabbitmq.client.ConnectionFactory;
import io.confluent.connect.utils.licensing.LicenseConfigUtil;
import io.confluent.connect.utils.validators.Validators;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.common.network.Mode;
import org.apache.kafka.common.security.ssl.SslFactory;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQConnectorConfig extends AbstractConfig {
    private static Logger log = LoggerFactory.getLogger(RabbitMQConnectorConfig.class);
    public static final String USERNAME_CONFIG = "rabbitmq.username";
    public static final String PASSWORD_CONFIG = "rabbitmq.password";
    public static final String VIRTUAL_HOST_CONFIG = "rabbitmq.virtual.host";
    public static final String REQUESTED_CHANNEL_MAX_CONFIG = "rabbitmq.requested.channel.max";
    public static final String REQUESTED_FRAME_MAX_CONFIG = "rabbitmq.requested.frame.max";
    public static final String CONNECTION_TIMEOUT_CONFIG = "rabbitmq.connection.timeout.ms";
    public static final String HANDSHAKE_TIMEOUT_CONFIG = "rabbitmq.handshake.timeout.ms";
    public static final String SHUTDOWN_TIMEOUT_CONFIG = "rabbitmq.shutdown.timeout.ms";
    public static final String REQUESTED_HEARTBEAT_CONFIG = "rabbitmq.requested.heartbeat.seconds";
    public static final String AUTOMATIC_RECOVERY_ENABLED_CONFIG = "rabbitmq.automatic.recovery.enabled";
    public static final String TOPOLOGY_RECOVERY_ENABLED_CONFIG = "rabbitmq.topology.recovery.enabled";
    public static final String NETWORK_RECOVERY_INTERVAL_CONFIG = "rabbitmq.network.recovery.interval.ms";
    public static final String HOST_CONFIG = "rabbitmq.host";
    public static final String PORT_CONFIG = "rabbitmq.port";
    static final String HOST_DOC = "The RabbitMQ host to connect to. See `ConnectionFactory.setHost(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setHost-java.lang.String->`_";
    static final String USERNAME_DOC = "The username to authenticate to RabbitMQ with. See `ConnectionFactory.setUsername(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setUsername-java.lang.String->`_";
    static final String PASSWORD_DOC = "The password to authenticate to RabbitMQ with. See `ConnectionFactory.setPassword(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setPassword-java.lang.String->`_";
    static final String VIRTUAL_HOST_DOC = "The virtual host to use when connecting to the broker. See `ConnectionFactory.setVirtualHost(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setVirtualHost-java.lang.String->`_";
    static final String REQUESTED_CHANNEL_MAX_DOC = "Initially requested maximum channel number. Zero for unlimited. See `ConnectionFactory.setRequestedChannelMax(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setRequestedChannelMax-int->`_";
    static final String REQUESTED_FRAME_MAX_DOC = "Initially requested maximum frame size, in octets. Zero for unlimited. See `ConnectionFactory.setRequestedFrameMax(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setRequestedFrameMax-int->`_";
    static final String CONNECTION_TIMEOUT_DOC = "Connection TCP establishment timeout in milliseconds. zero for infinite. See `ConnectionFactory.setConnectionTimeout(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setConnectionTimeout-int->`_";
    static final String HANDSHAKE_TIMEOUT_DOC = "The AMQP0-9-1 protocol handshake timeout, in milliseconds. See `ConnectionFactory.setHandshakeTimeout(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setHandshakeTimeout-int->`_";
    static final String SHUTDOWN_TIMEOUT_DOC = "Set the shutdown timeout. This is the amount of time that Consumer implementations have to continue working through deliveries (and other Consumer callbacks) after the connection has closed but before the ConsumerWorkService is torn down. If consumers exceed this timeout then any remaining queued deliveries (and other Consumer callbacks, *including* the Consumer's handleShutdownSignal() invocation) will be lost. See `ConnectionFactory.setShutdownTimeout(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setShutdownTimeout-int->`_";
    static final String REQUESTED_HEARTBEAT_DOC = "Set the requested heartbeat timeout. Heartbeat frames will be sent at about 1/2 the timeout interval. If server heartbeat timeout is configured to a non-zero value, this method can only be used to lower the value; otherwise any value provided by the client will be used. See `ConnectionFactory.setRequestedHeartbeat(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setRequestedHeartbeat-int->`_";
    static final String AUTOMATIC_RECOVERY_ENABLED_DOC = "Enables or disables automatic connection recovery. See `ConnectionFactory.setAutomaticRecoveryEnabled(boolean) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setAutomaticRecoveryEnabled-boolean->`_";
    static final String TOPOLOGY_RECOVERY_ENABLED_DOC = "Enables or disables topology recovery. See `ConnectionFactory.setTopologyRecoveryEnabled(boolean) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setTopologyRecoveryEnabled-boolean->`_";
    static final String NETWORK_RECOVERY_INTERVAL_DOC = "See `ConnectionFactory.setNetworkRecoveryInterval(long) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setNetworkRecoveryInterval-long->`_";
    static final String PORT_DOC = "The RabbitMQ port to connect to. See `ConnectionFactory.setPort(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setPort-int->`_";
    public final String username = this.getString("rabbitmq.username");
    public final Password password = this.getPassword("rabbitmq.password");
    public final String virtualHost = this.getString("rabbitmq.virtual.host");
    public final int requestedChannelMax = this.getInt("rabbitmq.requested.channel.max");
    public final int requestedFrameMax = this.getInt("rabbitmq.requested.frame.max");
    public final int connectionTimeout = this.getInt("rabbitmq.connection.timeout.ms");
    public final int handshakeTimeout = this.getInt("rabbitmq.handshake.timeout.ms");
    public final int shutdownTimeout = this.getInt("rabbitmq.shutdown.timeout.ms");
    public final int requestedHeartbeat = this.getInt("rabbitmq.requested.heartbeat.seconds");
    public final boolean automaticRecoveryEnabled = this.getBoolean("rabbitmq.automatic.recovery.enabled");
    public final boolean topologyRecoveryEnabled = this.getBoolean("rabbitmq.topology.recovery.enabled");
    public final long networkRecoveryInterval = (long)this.getInt("rabbitmq.network.recovery.interval.ms");
    public final String host = this.getString("rabbitmq.host");
    public final int port = this.getInt("rabbitmq.port");
    public final ConnectionFactory connectionFactory = this.connectionFactory();
    public static final String CONNECTION_SSL_CONFIG_PREFIX = "rabbitmq.https.";
    public static final String RABBITMQ_SECURITY_PROTOCOL_CONFIG = "rabbitmq.security.protocol";
    public final String securityProtocol = this.getString("rabbitmq.security.protocol");
    private static final String RABBITMQ_SECURITY_PROTOCOL_DOC = "The security protocol to use when connection to RabbitMQ. Values can be `PLAINTEXT` or `SSL`. If `PLAINTEXT` is passed, all configs prefixed by rabbitmq.https. or ssl. will be ignored.";
    private static final String SSL_GROUP = "Security";
    public static final String GROUP_CONNECTION = "Connection";

    private static void addSecurityConfigs(ConfigDef configDef) {
        int orderInGroup = 0;
        ConfigDef.Type var10002 = Type.STRING;
        String var10003 = SecurityProtocol.PLAINTEXT.name();
        ++orderInGroup;
        configDef.define("rabbitmq.security.protocol", var10002, var10003, Importance.MEDIUM, "The security protocol to use when connection to RabbitMQ. Values can be `PLAINTEXT` or `SSL`. If `PLAINTEXT` is passed, all configs prefixed by rabbitmq.https. or ssl. will be ignored.", "Security", orderInGroup, Width.SHORT, "Security protocol");
        ++orderInGroup;
        configDef.embed("rabbitmq.https.", "Security", orderInGroup, (new ConfigDef()).withClientSslSupport());
    }

    public RabbitMQConnectorConfig(ConfigDef definition, Map<?, ?> originals) {
        super(definition, originals);
    }

    public static ConfigDef config() {
        ConfigDef config = (new ConfigDef()).define(ConfigKeyBuilder.of("rabbitmq.host", Type.STRING).defaultValue("localhost").importance(Importance.HIGH).documentation("The RabbitMQ host to connect to. See `ConnectionFactory.setHost(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setHost-java.lang.String->`_").group("Connection").validator(Validators.hostnameOrIpAddress()).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.username", Type.STRING).defaultValue("guest").importance(Importance.HIGH).documentation("The username to authenticate to RabbitMQ with. See `ConnectionFactory.setUsername(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setUsername-java.lang.String->`_").group("Connection").internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.password", Type.PASSWORD).defaultValue(new Password("guest")).importance(Importance.HIGH).documentation("The password to authenticate to RabbitMQ with. See `ConnectionFactory.setPassword(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setPassword-java.lang.String->`_").group("Connection").internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.virtual.host", Type.STRING).defaultValue("/").importance(Importance.HIGH).documentation("The virtual host to use when connecting to the broker. See `ConnectionFactory.setVirtualHost(java.lang.String) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setVirtualHost-java.lang.String->`_").group("Connection").internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.requested.channel.max", Type.INT).defaultValue(2047).importance(Importance.LOW).documentation("Initially requested maximum channel number. Zero for unlimited. See `ConnectionFactory.setRequestedChannelMax(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setRequestedChannelMax-int->`_").group("Connection").validator(Validators.atLeast(0)).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.requested.frame.max", Type.INT).defaultValue(0).importance(Importance.LOW).documentation("Initially requested maximum frame size, in octets. Zero for unlimited. See `ConnectionFactory.setRequestedFrameMax(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setRequestedFrameMax-int->`_").group("Connection").validator(Validators.atLeast(0)).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.connection.timeout.ms", Type.INT).defaultValue(60000).importance(Importance.LOW).documentation("Connection TCP establishment timeout in milliseconds. zero for infinite. See `ConnectionFactory.setConnectionTimeout(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setConnectionTimeout-int->`_").group("Connection").validator(Validators.atLeast(0)).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.handshake.timeout.ms", Type.INT).defaultValue(10000).importance(Importance.LOW).documentation("The AMQP0-9-1 protocol handshake timeout, in milliseconds. See `ConnectionFactory.setHandshakeTimeout(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setHandshakeTimeout-int->`_").group("Connection").validator(Validators.atLeast(0)).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.shutdown.timeout.ms", Type.INT).defaultValue(10000).importance(Importance.LOW).documentation("Set the shutdown timeout. This is the amount of time that Consumer implementations have to continue working through deliveries (and other Consumer callbacks) after the connection has closed but before the ConsumerWorkService is torn down. If consumers exceed this timeout then any remaining queued deliveries (and other Consumer callbacks, *including* the Consumer's handleShutdownSignal() invocation) will be lost. See `ConnectionFactory.setShutdownTimeout(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setShutdownTimeout-int->`_").group("Connection").validator(Validators.atLeast(0)).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.requested.heartbeat.seconds", Type.INT).defaultValue(60).importance(Importance.LOW).documentation("Set the requested heartbeat timeout. Heartbeat frames will be sent at about 1/2 the timeout interval. If server heartbeat timeout is configured to a non-zero value, this method can only be used to lower the value; otherwise any value provided by the client will be used. See `ConnectionFactory.setRequestedHeartbeat(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setRequestedHeartbeat-int->`_").group("Connection").validator(Validators.atLeast(0)).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.automatic.recovery.enabled", Type.BOOLEAN).defaultValue(true).importance(Importance.LOW).documentation("Enables or disables automatic connection recovery. See `ConnectionFactory.setAutomaticRecoveryEnabled(boolean) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setAutomaticRecoveryEnabled-boolean->`_").group("Connection").internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.topology.recovery.enabled", Type.BOOLEAN).defaultValue(true).importance(Importance.LOW).documentation("Enables or disables topology recovery. See `ConnectionFactory.setTopologyRecoveryEnabled(boolean) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setTopologyRecoveryEnabled-boolean->`_").group("Connection").internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.network.recovery.interval.ms", Type.INT).defaultValue(10000).importance(Importance.LOW).documentation("See `ConnectionFactory.setNetworkRecoveryInterval(long) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setNetworkRecoveryInterval-long->`_").group("Connection").validator(Validators.atLeast(0)).internalConfig(false).build()).define(ConfigKeyBuilder.of("rabbitmq.port", Type.INT).defaultValue(5672).importance(Importance.MEDIUM).documentation("The RabbitMQ port to connect to. See `ConnectionFactory.setPort(int) <https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/ConnectionFactory.html#setPort-int->`_").group("Connection").validator(Validators.portNumber()).internalConfig(false).build());
        addSecurityConfigs(config);
        return LicenseConfigUtil.addToConfigDef(config);
    }

    public final ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(this.host);
        connectionFactory.setPort(this.port);
        connectionFactory.setUsername(this.username);
        connectionFactory.setPassword(this.password.value());
        connectionFactory.setVirtualHost(this.virtualHost);
        connectionFactory.setRequestedChannelMax(this.requestedChannelMax);
        connectionFactory.setRequestedFrameMax(this.requestedFrameMax);
        connectionFactory.setConnectionTimeout(this.connectionTimeout);
        connectionFactory.setHandshakeTimeout(this.handshakeTimeout);
        connectionFactory.setShutdownTimeout(this.shutdownTimeout);
        connectionFactory.setRequestedHeartbeat(this.requestedHeartbeat);
        connectionFactory.setAutomaticRecoveryEnabled(this.automaticRecoveryEnabled);
        connectionFactory.setTopologyRecoveryEnabled(this.topologyRecoveryEnabled);
        connectionFactory.setNetworkRecoveryInterval(this.networkRecoveryInterval);
        if (SecurityProtocol.SSL.equals(SecurityProtocol.valueOf(this.securityProtocol))) {
            connectionFactory.useSslProtocol(sslContext(this.sslConfigs()));
        }

        return connectionFactory;
    }

    public Map<String, Object> sslConfigs() {
        return (new ConfigDef()).withClientSslSupport().parse(this.originalsWithPrefix("rabbitmq.https."));
    }

    public static SSLContext sslContext(Map<String, Object> sslConfigs) {
        SslFactory kafkaSslFactory = new SslFactory(Mode.CLIENT);
        kafkaSslFactory.configure(sslConfigs);

        Object sslEngine;
        try {
            sslEngine = SslFactory.class.getDeclaredMethod("sslEngineBuilder").invoke(kafkaSslFactory);
            log.debug("Using AK 2.2-2.5 SslFactory methods.");
        } catch (Exception var7) {
            log.debug("Could not find Ak 2.3-2.5 methods for SslFactory. Trying AK 2.6+ methods for SslFactory.");

            try {
                sslEngine = SslFactory.class.getDeclaredMethod("sslEngineFactory").invoke(kafkaSslFactory);
                log.debug("Using AK 2.6+ SslFactory methods.");
            } catch (Exception var6) {
                throw new ConnectException("Failed to find methods for SslFactory.", var6);
            }
        }

        try {
            return (SSLContext)sslEngine.getClass().getDeclaredMethod("sslContext").invoke(sslEngine);
        } catch (Exception var5) {
            throw new ConnectException("Could not create SSLContext.", var5);
        }
    }
}
