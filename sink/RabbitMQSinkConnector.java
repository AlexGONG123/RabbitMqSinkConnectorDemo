//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.rabbitmq.sink;

import io.confluent.connect.rabbitmq.sink.validation.RabbitMQSinkConfigValidation;
import io.confluent.connect.utils.Version;
import io.confluent.connect.utils.licensing.ConnectLicenseManager;
import io.confluent.connect.utils.licensing.LicenseConfigUtil;
import io.confluent.connect.utils.validators.all.ConfigValidation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQSinkConnector extends SinkConnector {
    private static Logger log = LoggerFactory.getLogger(RabbitMQSinkConnector.class);
    RabbitMQSinkConnectorConfig config;
    ConnectLicenseManager licenseManager;

    public RabbitMQSinkConnector() {
    }

    public void start(Map<String, String> settings) {
        this.config = new RabbitMQSinkConnectorConfig(settings);
        this.licenseManager = LicenseConfigUtil.createLicenseManager(this.config);
        this.doStart();
    }

    void doStart() {
        this.licenseManager.registerOrValidateLicense();
    }

    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList();
        Map<String, String> taskConfig = new HashMap(this.config.originalsStrings());

        for(int i = 0; i < maxTasks; ++i) {
            configs.add(taskConfig);
        }

        return configs;
    }

    public void stop() {
    }

    public ConfigDef config() {
        return RabbitMQSinkConnectorConfig.config();
    }

    public Class<? extends Task> taskClass() {
        return RabbitMQSinkTask.class;
    }

    public String version() {
        return Version.forClass(this.getClass());
    }

    public Config validate(Map<String, String> connectorConfigs) {
        return (new RabbitMQSinkConfigValidation(this.config(), connectorConfigs, new ConfigValidation.ConfigValidator[0])).validate();
    }
}
