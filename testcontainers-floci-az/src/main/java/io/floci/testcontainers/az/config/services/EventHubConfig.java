package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Event Hubs of Floci Azure.
 *
 * <p>Namespaces are backed by an ActiveMQ Artemis sidecar (AMQP) and optionally a Redpanda sidecar
 * (Kafka), both published on the Docker host. In {@code mocked} mode only the management plane
 * is available.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EventHubConfig config = EventHubConfig.builder()
 *     .mocked(true)
 *     .defaultNamespace("myNamespace")
 *     .entities("orders:2,payments:1")
 *     .build();
 * }</pre>
 */
public class EventHubConfig extends AbstractServiceConfig<EventHubConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = false;
    private static final String DEFAULT_DEFAULT_NAMESPACE = "emulatorNs1";
    private static final String DEFAULT_ENTITIES = "eh1:4";
    private static final int DEFAULT_AMQP_PORT = 5672;
    private static final int DEFAULT_AMQP_TLS_PORT = 5671;
    private static final boolean DEFAULT_KAFKA_ENABLED = false;
    private static final int DEFAULT_KAFKA_PORT = 9093;
    private static final String DEFAULT_ARTEMIS_IMAGE = "apache/activemq-artemis:2.44.0";
    private static final String DEFAULT_REDPANDA_IMAGE = "redpandadata/redpanda:latest";
    private static final String DEFAULT_CONSUMER_GROUPS = "$Default,my-consumer-group";

    private final boolean mocked;
    private final String defaultNamespace;
    private final String entities;
    private final int amqpPort;
    private final int amqpTlsPort;
    private final boolean kafkaEnabled;
    private final int kafkaPort;
    private final String artemisImage;
    private final String redpandaImage;
    private final String consumerGroups;

    private EventHubConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.defaultNamespace = builder.defaultNamespace;
        this.entities = builder.entities;
        this.amqpPort = builder.amqpPort;
        this.amqpTlsPort = builder.amqpTlsPort;
        this.kafkaEnabled = builder.kafkaEnabled;
        this.kafkaPort = builder.kafkaPort;
        this.artemisImage = builder.artemisImage;
        this.redpandaImage = builder.redpandaImage;
        this.consumerGroups = builder.consumerGroups;
    }

    /**
     * Returns a new {@link Builder} for this configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new {@link Builder} for this configuration, initialized with the current
     * values of this instance.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns whether no Artemis sidecar is started; the service responds to management calls but
     * the AMQP data plane is unavailable.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the name of the default Event Hubs namespace.
     *
     * @return the default namespace
     */
    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    /**
     * Returns the event hubs created in each namespace, as comma-separated
     * {@code name:partitions} pairs (e.g. {@code eh1:4,eh2:2}).
     *
     * @return the event hub definitions
     */
    public String getEntities() {
        return entities;
    }

    /**
     * Returns the host port the AMQP endpoint of the Artemis sidecar is published on.
     *
     * @return the AMQP port
     */
    public int getAmqpPort() {
        return amqpPort;
    }

    /**
     * Returns the host port the AMQP-over-TLS endpoint of the Artemis sidecar is published on,
     * used by clients that require TLS.
     *
     * @return the AMQP TLS port
     */
    public int getAmqpTlsPort() {
        return amqpTlsPort;
    }

    /**
     * Returns whether the Kafka endpoint (Redpanda sidecar) is started.
     *
     * @return {@code true} if the Kafka endpoint is enabled
     */
    public boolean isKafkaEnabled() {
        return kafkaEnabled;
    }

    /**
     * Returns the host port the Kafka endpoint is published on.
     *
     * @return the Kafka port
     */
    public int getKafkaPort() {
        return kafkaPort;
    }

    /**
     * Returns the Docker image of the Artemis (AMQP) sidecar.
     *
     * @return the Docker image
     */
    public String getArtemisImage() {
        return artemisImage;
    }

    /**
     * Returns the Docker image of the Redpanda (Kafka) sidecar.
     *
     * @return the Docker image
     */
    public String getRedpandaImage() {
        return redpandaImage;
    }

    /**
     * Returns the consumer groups created on every event hub, comma-separated.
     *
     * @return the consumer group names
     */
    public String getConsumerGroups() {
        return consumerGroups;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_DEFAULT_NAMESPACE", defaultNamespace);
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_ENTITIES", entities);
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_PORT", String.valueOf(amqpPort));
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_AMQP_TLS_PORT", String.valueOf(amqpTlsPort));
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_ENABLED", String.valueOf(kafkaEnabled));
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_KAFKA_PORT", String.valueOf(kafkaPort));
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_ARTEMIS_IMAGE", artemisImage);
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_REDPANDA_IMAGE", redpandaImage);
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_HUB_CONSUMER_GROUPS", consumerGroups);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link EventHubConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EventHubConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String defaultNamespace = DEFAULT_DEFAULT_NAMESPACE;
        private String entities = DEFAULT_ENTITIES;
        private int amqpPort = DEFAULT_AMQP_PORT;
        private int amqpTlsPort = DEFAULT_AMQP_TLS_PORT;
        private boolean kafkaEnabled = DEFAULT_KAFKA_ENABLED;
        private int kafkaPort = DEFAULT_KAFKA_PORT;
        private String artemisImage = DEFAULT_ARTEMIS_IMAGE;
        private String redpandaImage = DEFAULT_REDPANDA_IMAGE;
        private String consumerGroups = DEFAULT_CONSUMER_GROUPS;

        private Builder() {
            // Allow instantiation only via EventHubConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EventHubConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EventHubConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.defaultNamespace = instance.defaultNamespace;
            this.entities = instance.entities;
            this.amqpPort = instance.amqpPort;
            this.amqpTlsPort = instance.amqpTlsPort;
            this.kafkaEnabled = instance.kafkaEnabled;
            this.kafkaPort = instance.kafkaPort;
            this.artemisImage = instance.artemisImage;
            this.redpandaImage = instance.redpandaImage;
            this.consumerGroups = instance.consumerGroups;
        }

        /**
         * Sets whether no Artemis sidecar is started; the service responds to management calls but the
         * AMQP data plane is unavailable. Useful for tests without Docker.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the name of the default Event Hubs namespace.
         *
         * @param defaultNamespace the namespace name (default {@value DEFAULT_DEFAULT_NAMESPACE})
         * @return this builder
         */
        public Builder defaultNamespace(String defaultNamespace) {
            this.defaultNamespace = defaultNamespace;
            return this;
        }

        /**
         * Sets the event hubs created in each namespace, as comma-separated {@code name:partitions}
         * pairs (e.g. {@code eh1:4,eh2:2}).
         *
         * @param entities the event hub definitions (default {@value DEFAULT_ENTITIES})
         * @return this builder
         */
        public Builder entities(String entities) {
            this.entities = entities;
            return this;
        }

        /**
         * Sets the host port the AMQP endpoint of the Artemis sidecar is published on.
         *
         * @param amqpPort the AMQP port (default {@value DEFAULT_AMQP_PORT})
         * @return this builder
         */
        public Builder amqpPort(int amqpPort) {
            this.amqpPort = amqpPort;
            return this;
        }

        /**
         * Sets the host port the AMQP-over-TLS endpoint of the Artemis sidecar is published on,
         * used by clients that require TLS.
         *
         * @param amqpTlsPort the AMQP TLS port (default {@value DEFAULT_AMQP_TLS_PORT})
         * @return this builder
         */
        public Builder amqpTlsPort(int amqpTlsPort) {
            this.amqpTlsPort = amqpTlsPort;
            return this;
        }

        /**
         * Sets whether the Kafka endpoint (Redpanda sidecar) is started.
         *
         * @param kafkaEnabled {@code true} to enable the Kafka endpoint (default {@value DEFAULT_KAFKA_ENABLED})
         * @return this builder
         */
        public Builder kafkaEnabled(boolean kafkaEnabled) {
            this.kafkaEnabled = kafkaEnabled;
            return this;
        }

        /**
         * Sets the host port the Kafka endpoint is published on.
         *
         * @param kafkaPort the Kafka port (default {@value DEFAULT_KAFKA_PORT})
         * @return this builder
         */
        public Builder kafkaPort(int kafkaPort) {
            this.kafkaPort = kafkaPort;
            return this;
        }

        /**
         * Sets the Docker image of the Artemis (AMQP) sidecar.
         *
         * @param artemisImage the Docker image (default {@value DEFAULT_ARTEMIS_IMAGE})
         * @return this builder
         */
        public Builder artemisImage(String artemisImage) {
            this.artemisImage = artemisImage;
            return this;
        }

        /**
         * Sets the Docker image of the Redpanda (Kafka) sidecar.
         *
         * @param redpandaImage the Docker image (default {@value DEFAULT_REDPANDA_IMAGE})
         * @return this builder
         */
        public Builder redpandaImage(String redpandaImage) {
            this.redpandaImage = redpandaImage;
            return this;
        }

        /**
         * Sets the consumer groups created on every event hub, comma-separated
         * (e.g. {@code $Default,my-group}).
         *
         * @param consumerGroups the consumer group names (default {@value DEFAULT_CONSUMER_GROUPS})
         * @return this builder
         */
        public Builder consumerGroups(String consumerGroups) {
            this.consumerGroups = consumerGroups;
            return this;
        }

        /**
         * Creates an immutable {@link EventHubConfig} from this builder.
         *
         * @return the Event Hubs configuration
         */
        @Override
        public EventHubConfig build() {
            return new EventHubConfig(this);
        }
    }
}
