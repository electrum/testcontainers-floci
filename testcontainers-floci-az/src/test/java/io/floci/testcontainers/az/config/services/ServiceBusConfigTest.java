package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.TransferableCopyInspector;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ServiceBusConfigTest {

    @Test
    void shouldApplyDefaultServiceBusConfig() {
        ServiceBusConfig config = ServiceBusConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.isStartOnBoot()).isFalse();
        assertThat(config.getTopologyFile()).isEmpty();
        assertThat(config.getTopology()).isEmpty();
        assertThat(config.getAmqpPort()).isEqualTo(5673);
        assertThat(config.getAmqpTlsPort()).isEqualTo(5674);
        assertThat(config.getArtemisImage()).isEqualTo("apache/activemq-artemis:2.44.0");
        assertThat(config.getMaxDeliveryCount()).isEqualTo(10);
        assertThat(config.getLockDurationSeconds()).isEqualTo(60L);
    }

    @Test
    void shouldApplyCustomServiceBusConfig() {
        ServiceBusConfig config = ServiceBusConfig.builder()
                .enabled(false)
                .mocked(false)
                .startOnBoot(true)
                .topologyFile("/config/Config.json")
                .amqpPort(15673)
                .amqpTlsPort(15674)
                .artemisImage("apache/activemq-artemis:2.45.0")
                .maxDeliveryCount(3)
                .lockDurationSeconds(30L)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.isStartOnBoot()).isTrue();
        assertThat(config.getTopologyFile()).contains("/config/Config.json");
        assertThat(config.getAmqpPort()).isEqualTo(15673);
        assertThat(config.getAmqpTlsPort()).isEqualTo(15674);
        assertThat(config.getArtemisImage()).isEqualTo("apache/activemq-artemis:2.45.0");
        assertThat(config.getMaxDeliveryCount()).isEqualTo(3);
        assertThat(config.getLockDurationSeconds()).isEqualTo(30L);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceBusConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_START_ON_BOOT", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_TOPOLOGY_FILE")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_PORT", "5673")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_TLS_PORT", "5674")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_ARTEMIS_IMAGE", "apache/activemq-artemis:2.44.0")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_MAX_DELIVERY_COUNT", "10")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_LOCK_DURATION_SECONDS", "60");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceBusConfig.builder()
                .mocked(false)
                .startOnBoot(true)
                .topologyFile("/config/Config.json")
                .amqpPort(15673)
                .amqpTlsPort(15674)
                .artemisImage("apache/activemq-artemis:2.45.0")
                .maxDeliveryCount(3)
                .lockDurationSeconds(30L)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_START_ON_BOOT", "true")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_TOPOLOGY_FILE", "/config/Config.json")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_PORT", "15673")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_TLS_PORT", "15674")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_ARTEMIS_IMAGE", "apache/activemq-artemis:2.45.0")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_MAX_DELIVERY_COUNT", "3")
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_LOCK_DURATION_SECONDS", "30");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceBusConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_START_ON_BOOT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_TOPOLOGY_FILE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_TLS_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_ARTEMIS_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_MAX_DELIVERY_COUNT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SERVICE_BUS_LOCK_DURATION_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ServiceBusConfig config = ServiceBusConfig.builder()
                .enabled(false)
                .mocked(false)
                .startOnBoot(true)
                .topologyFile("/config/Config.json")
                .amqpPort(15673)
                .amqpTlsPort(15674)
                .artemisImage("apache/activemq-artemis:2.45.0")
                .maxDeliveryCount(3)
                .lockDurationSeconds(30L)
                .build();
        ServiceBusConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isFalse();
        assertThat(copy.isStartOnBoot()).isTrue();
        assertThat(copy.getTopologyFile()).contains("/config/Config.json");
        assertThat(copy.getAmqpPort()).isEqualTo(15673);
        assertThat(copy.getAmqpTlsPort()).isEqualTo(15674);
        assertThat(copy.getArtemisImage()).isEqualTo("apache/activemq-artemis:2.45.0");
        assertThat(copy.getMaxDeliveryCount()).isEqualTo(3);
        assertThat(copy.getLockDurationSeconds()).isEqualTo(30L);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(ServiceBusConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(ServiceBusConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(ServiceBusConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldCopyTopologyContentIntoContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceBusConfig config = ServiceBusConfig.builder().topology("{\"UserConfig\":{}}").build();

        config.applyEnvVarsToContainer(container);
        config.applyFileMountsToContainer(container);

        String topologyFile = config.getTopologyFile().orElseThrow();
        assertThat(topologyFile).startsWith("/tmp/floci-az-servicebus-topology-").endsWith(".json");
        assertThat(config.getTopology()).contains("{\"UserConfig\":{}}");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_AZ_SERVICES_SERVICE_BUS_TOPOLOGY_FILE", topologyFile);
        assertThat(TransferableCopyInspector.contentCopiedTo(container, topologyFile)).contains("{\"UserConfig\":{}}");
    }

    @Test
    void shouldNotCopyAnythingForExplicitTopologyFile() {
        GenericContainer<?> container = genericContainer();
        ServiceBusConfig config = ServiceBusConfig.builder().topologyFile("/config/Config.json").build();

        config.applyFileMountsToContainer(container);

        assertThat(config.getTopology()).isEmpty();
        assertThat(TransferableCopyInspector.pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldReplaceTopologyFileAndContentWithEachOther() {
        ServiceBusConfig fileAfterContent = ServiceBusConfig.builder().topology("{}").topologyFile("/config/Config.json").build();
        ServiceBusConfig contentAfterFile = ServiceBusConfig.builder().topologyFile("/config/Config.json").topology("{}").build();

        assertThat(fileAfterContent.getTopologyFile()).contains("/config/Config.json");
        assertThat(fileAfterContent.getTopology()).isEmpty();
        assertThat(contentAfterFile.getTopologyFile()).hasValueSatisfying(path -> assertThat(path).startsWith("/tmp/"));
        assertThat(contentAfterFile.getTopology()).contains("{}");
    }

    @Test
    void shouldPreserveTopologyContentOnToBuilder() {
        ServiceBusConfig config = ServiceBusConfig.builder().topology("{}").build();

        ServiceBusConfig copy = config.toBuilder().build();

        assertThat(copy.getTopology()).contains("{}");
        assertThat(copy.getTopologyFile()).isEqualTo(config.getTopologyFile());
    }
}
