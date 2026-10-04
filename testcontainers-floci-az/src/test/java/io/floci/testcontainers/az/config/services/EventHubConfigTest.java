package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class EventHubConfigTest {

    @Test
    void shouldApplyDefaultEventHubConfig() {
        EventHubConfig config = EventHubConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getDefaultNamespace()).isEqualTo("emulatorNs1");
        assertThat(config.getEntities()).isEqualTo("eh1:4");
        assertThat(config.getAmqpPort()).isEqualTo(5672);
        assertThat(config.getAmqpTlsPort()).isEqualTo(5671);
        assertThat(config.isKafkaEnabled()).isFalse();
        assertThat(config.getKafkaPort()).isEqualTo(9093);
        assertThat(config.getArtemisImage()).isEqualTo("apache/activemq-artemis:2.44.0");
        assertThat(config.getRedpandaImage()).isEqualTo("redpandadata/redpanda:latest");
        assertThat(config.getConsumerGroups()).isEqualTo("$Default,my-consumer-group");
    }

    @Test
    void shouldApplyCustomEventHubConfig() {
        EventHubConfig config = EventHubConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultNamespace("myNamespace")
                .entities("orders:2,payments:1")
                .amqpPort(15672)
                .amqpTlsPort(15671)
                .kafkaEnabled(true)
                .kafkaPort(19093)
                .artemisImage("apache/activemq-artemis:2.45.0")
                .redpandaImage("redpandadata/redpanda:v25.1.1")
                .consumerGroups("$Default,analytics")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getDefaultNamespace()).isEqualTo("myNamespace");
        assertThat(config.getEntities()).isEqualTo("orders:2,payments:1");
        assertThat(config.getAmqpPort()).isEqualTo(15672);
        assertThat(config.getAmqpTlsPort()).isEqualTo(15671);
        assertThat(config.isKafkaEnabled()).isTrue();
        assertThat(config.getKafkaPort()).isEqualTo(19093);
        assertThat(config.getArtemisImage()).isEqualTo("apache/activemq-artemis:2.45.0");
        assertThat(config.getRedpandaImage()).isEqualTo("redpandadata/redpanda:v25.1.1");
        assertThat(config.getConsumerGroups()).isEqualTo("$Default,analytics");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EventHubConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_DEFAULT_NAMESPACE", "emulatorNs1")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_ENTITIES", "eh1:4")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_PORT", "5672")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_TLS_PORT", "5671")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_ENABLED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_PORT", "9093")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_ARTEMIS_IMAGE", "apache/activemq-artemis:2.44.0")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_REDPANDA_IMAGE", "redpandadata/redpanda:latest")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_CONSUMER_GROUPS", "$Default,my-consumer-group");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        EventHubConfig.builder()
                .mocked(true)
                .defaultNamespace("myNamespace")
                .entities("orders:2,payments:1")
                .amqpPort(15672)
                .amqpTlsPort(15671)
                .kafkaEnabled(true)
                .kafkaPort(19093)
                .artemisImage("apache/activemq-artemis:2.45.0")
                .redpandaImage("redpandadata/redpanda:v25.1.1")
                .consumerGroups("$Default,analytics")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_DEFAULT_NAMESPACE", "myNamespace")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_ENTITIES", "orders:2,payments:1")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_PORT", "15672")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_TLS_PORT", "15671")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_PORT", "19093")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_ARTEMIS_IMAGE", "apache/activemq-artemis:2.45.0")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_REDPANDA_IMAGE", "redpandadata/redpanda:v25.1.1")
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_CONSUMER_GROUPS", "$Default,analytics");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        EventHubConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_EVENT_HUB_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_DEFAULT_NAMESPACE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_ENTITIES")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_TLS_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_ENABLED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_ARTEMIS_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_REDPANDA_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_EVENT_HUB_CONSUMER_GROUPS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        EventHubConfig config = EventHubConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultNamespace("myNamespace")
                .entities("orders:2,payments:1")
                .amqpPort(15672)
                .amqpTlsPort(15671)
                .kafkaEnabled(true)
                .kafkaPort(19093)
                .artemisImage("apache/activemq-artemis:2.45.0")
                .redpandaImage("redpandadata/redpanda:v25.1.1")
                .consumerGroups("$Default,analytics")
                .build();
        EventHubConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getDefaultNamespace()).isEqualTo("myNamespace");
        assertThat(copy.getEntities()).isEqualTo("orders:2,payments:1");
        assertThat(copy.getAmqpPort()).isEqualTo(15672);
        assertThat(copy.getAmqpTlsPort()).isEqualTo(15671);
        assertThat(copy.isKafkaEnabled()).isTrue();
        assertThat(copy.getKafkaPort()).isEqualTo(19093);
        assertThat(copy.getArtemisImage()).isEqualTo("apache/activemq-artemis:2.45.0");
        assertThat(copy.getRedpandaImage()).isEqualTo("redpandadata/redpanda:v25.1.1");
        assertThat(copy.getConsumerGroups()).isEqualTo("$Default,analytics");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(EventHubConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(EventHubConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(EventHubConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
