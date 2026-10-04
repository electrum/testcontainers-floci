package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;
import org.testcontainers.images.builder.Transferable;

import java.util.Optional;
import java.util.UUID;

/**
 * Configuration for Azure Service Bus of Floci Azure.
 *
 * <p>Each namespace is backed by an ActiveMQ Artemis sidecar (AMQP 1.0 data plane) whose ports floci-az
 * publishes on the Docker host. By default the service is {@code mocked}: only the management plane is
 * available.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ServiceBusConfig config = ServiceBusConfig.builder()
 *     .mocked(false)
 *     .startOnBoot(true)
 *     .topology(Files.readString(Path.of("Config.json")))
 *     .build();
 * }</pre>
 */
public class ServiceBusConfig extends AbstractServiceConfig<ServiceBusConfig.Builder> {

    private static final String TOPOLOGY_FILE_PREFIX = "/tmp/floci-az-servicebus-topology-";
    private static final String TOPOLOGY_FILE_SUFFIX = ".json";

    private static final boolean DEFAULT_MOCKED = true;
    private static final boolean DEFAULT_START_ON_BOOT = false;
    private static final int DEFAULT_AMQP_PORT = 5673;
    private static final int DEFAULT_AMQP_TLS_PORT = 5674;
    private static final String DEFAULT_ARTEMIS_IMAGE = "apache/activemq-artemis:2.44.0";
    private static final int DEFAULT_MAX_DELIVERY_COUNT = 10;
    private static final long DEFAULT_LOCK_DURATION_SECONDS = 60L;

    private final boolean mocked;
    private final boolean startOnBoot;
    private final String topologyFile;
    private final String topology;
    private final int amqpPort;
    private final int amqpTlsPort;
    private final String artemisImage;
    private final int maxDeliveryCount;
    private final long lockDurationSeconds;

    private ServiceBusConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.startOnBoot = builder.startOnBoot;
        this.topologyFile = builder.topologyFile;
        this.topology = builder.topology;
        this.amqpPort = builder.amqpPort;
        this.amqpTlsPort = builder.amqpTlsPort;
        this.artemisImage = builder.artemisImage;
        this.maxDeliveryCount = builder.maxDeliveryCount;
        this.lockDurationSeconds = builder.lockDurationSeconds;
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
     * Returns whether no Artemis sidecar is started; the service responds to management calls, but the
     * AMQP data plane is unavailable.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns whether the {@code default} namespace is started together with the emulator instead of
     * on the first entity-management call.
     *
     * @return {@code true} if the default namespace starts on boot
     */
    public boolean isStartOnBoot() {
        return startOnBoot;
    }

    /**
     * Returns the path, inside the container, of the declarative topology file (in the {@code Config.json}
     * format of the official Service Bus emulator) that floci-az applies at startup. When empty, floci-az probes
     * the mount path of the official emulator ({@code /ServiceBus_Emulator/ConfigFiles/Config.json}).
     *
     * <p>The path is either the one passed to {@link Builder#topologyFile(String)} verbatim, or a generated path
     * pointing at the file whose content was passed to {@link Builder#topology(String)}.
     *
     * @return the container path of the topology file, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getTopologyFile() {
        return Optional.ofNullable(topologyFile);
    }

    /**
     * Returns the raw topology file content supplied via {@link Builder#topology(String)}, if any. When present,
     * this content is copied into the container at {@link #getTopologyFile()}.
     *
     * @return the topology content, or {@link Optional#empty()} if the topology was not configured by content
     */
    public Optional<String> getTopology() {
        return Optional.ofNullable(topology);
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
     * Returns the host port the AMQP-over-TLS endpoint of the Artemis sidecar is published on.
     *
     * @return the AMQP TLS port
     */
    public int getAmqpTlsPort() {
        return amqpTlsPort;
    }

    /**
     * Returns the Docker image of the Artemis sidecar.
     *
     * @return the Docker image
     */
    public String getArtemisImage() {
        return artemisImage;
    }

    /**
     * Returns the default maximum delivery count before a message is dead-lettered.
     *
     * @return the maximum delivery count
     */
    public int getMaxDeliveryCount() {
        return maxDeliveryCount;
    }

    /**
     * Returns the default message lock duration in seconds.
     *
     * @return the lock duration in seconds
     */
    public long getLockDurationSeconds() {
        return lockDurationSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_START_ON_BOOT", String.valueOf(startOnBoot));
            container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_PORT", String.valueOf(amqpPort));
            container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_AMQP_TLS_PORT", String.valueOf(amqpTlsPort));
            container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_ARTEMIS_IMAGE", artemisImage);
            container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_MAX_DELIVERY_COUNT", String.valueOf(maxDeliveryCount));
            container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_LOCK_DURATION_SECONDS", String.valueOf(lockDurationSeconds));

            if (topologyFile != null) {
                container.withEnv("FLOCI_AZ_SERVICES_SERVICE_BUS_TOPOLOGY_FILE", topologyFile);
            }
        }
    }

    @Override
    public void applyFileMountsToContainer(Container<?> container) {
        if (isEnabled() && topologyFile != null && topology != null) {
            container.withCopyToContainer(Transferable.of(topology), topologyFile);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link ServiceBusConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ServiceBusConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private boolean startOnBoot = DEFAULT_START_ON_BOOT;
        private String topologyFile;
        private String topology;
        private int amqpPort = DEFAULT_AMQP_PORT;
        private int amqpTlsPort = DEFAULT_AMQP_TLS_PORT;
        private String artemisImage = DEFAULT_ARTEMIS_IMAGE;
        private int maxDeliveryCount = DEFAULT_MAX_DELIVERY_COUNT;
        private long lockDurationSeconds = DEFAULT_LOCK_DURATION_SECONDS;

        private Builder() {
            // Allow instantiation only via ServiceBusConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ServiceBusConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ServiceBusConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.startOnBoot = instance.startOnBoot;
            this.topologyFile = instance.topologyFile;
            this.topology = instance.topology;
            this.amqpPort = instance.amqpPort;
            this.amqpTlsPort = instance.amqpTlsPort;
            this.artemisImage = instance.artemisImage;
            this.maxDeliveryCount = instance.maxDeliveryCount;
            this.lockDurationSeconds = instance.lockDurationSeconds;
        }

        /**
         * Sets whether no Artemis sidecar is started; the service responds to management calls, but the
         * AMQP data plane is unavailable.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets whether the {@code default} namespace is started together with the emulator instead of on
         * the first entity-management call, so the AMQP port is listening as soon as floci-az is up.
         *
         * @param startOnBoot {@code true} to start the default namespace on boot (default {@value DEFAULT_START_ON_BOOT})
         * @return this builder
         */
        public Builder startOnBoot(boolean startOnBoot) {
            this.startOnBoot = startOnBoot;
            return this;
        }

        /**
         * Sets the path, inside the container, of a declarative topology file (in the {@code Config.json} format
         * of the official Service Bus emulator) that already exists in the container (for example one added
         * through a volume or another {@code withCopy*} call). floci-az creates the declared namespaces, queues,
         * topics and subscriptions at startup.
         *
         * <p>Use {@link #topology(String)} instead to hand over just the file content and let
         * {@link ServiceBusConfig} take care of placing the file into the container.
         *
         * <p>Calling this method clears any content previously set via {@link #topology(String)}.
         *
         * @param topologyFile the container path of the topology file, or {@code null} for none
         * @return this builder
         */
        public Builder topologyFile(String topologyFile) {
            this.topologyFile = topologyFile;
            this.topology = null;
            return this;
        }

        /**
         * Sets the content of a declarative topology file (in the {@code Config.json} format of the official
         * Service Bus emulator) that floci-az applies at startup.
         *
         * <p>The content is copied into the container under a generated, randomized path, which is then used as
         * {@link ServiceBusConfig#getTopologyFile()}.
         *
         * <p>Calling this method clears any path previously set via {@link #topologyFile(String)}.
         *
         * @param topology the topology file content, or {@code null} for none
         * @return this builder
         */
        public Builder topology(String topology) {
            this.topology = topology;
            this.topologyFile = null;
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
         * Sets the host port the AMQP-over-TLS endpoint of the Artemis sidecar is published on.
         *
         * @param amqpTlsPort the AMQP TLS port (default {@value DEFAULT_AMQP_TLS_PORT})
         * @return this builder
         */
        public Builder amqpTlsPort(int amqpTlsPort) {
            this.amqpTlsPort = amqpTlsPort;
            return this;
        }

        /**
         * Sets the Docker image of the Artemis sidecar.
         *
         * @param artemisImage the Docker image (default {@value DEFAULT_ARTEMIS_IMAGE})
         * @return this builder
         */
        public Builder artemisImage(String artemisImage) {
            this.artemisImage = artemisImage;
            return this;
        }

        /**
         * Sets the default maximum delivery count before a message is dead-lettered.
         *
         * @param maxDeliveryCount the maximum delivery count (default {@value DEFAULT_MAX_DELIVERY_COUNT})
         * @return this builder
         */
        public Builder maxDeliveryCount(int maxDeliveryCount) {
            this.maxDeliveryCount = maxDeliveryCount;
            return this;
        }

        /**
         * Sets the default message lock duration in seconds.
         *
         * @param lockDurationSeconds the lock duration in seconds (default {@value DEFAULT_LOCK_DURATION_SECONDS})
         * @return this builder
         */
        public Builder lockDurationSeconds(long lockDurationSeconds) {
            this.lockDurationSeconds = lockDurationSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link ServiceBusConfig} from this builder.
         *
         * @return the Service Bus configuration
         */
        @Override
        public ServiceBusConfig build() {
            if (topology != null && topologyFile == null) {
                this.topologyFile = TOPOLOGY_FILE_PREFIX + UUID.randomUUID() + TOPOLOGY_FILE_SUFFIX;
            }
            return new ServiceBusConfig(this);
        }
    }
}
