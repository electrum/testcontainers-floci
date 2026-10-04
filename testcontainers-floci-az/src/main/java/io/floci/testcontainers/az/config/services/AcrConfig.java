package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Container Registry of Floci Azure.
 *
 * <p>Registries are backed by a shared Docker Registry V2 container that floci-az publishes on the Docker
 * host.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AcrConfig config = AcrConfig.builder()
 *     .mocked(true)
 *     .defaultImage("registry:3")
 *     .portRange(15000, 5)
 *     .build();
 * }</pre>
 */
public class AcrConfig extends AbstractServiceConfig<AcrConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = false;
    private static final String DEFAULT_DEFAULT_IMAGE = "registry:2";
    private static final int DEFAULT_BASE_PORT = 5000;
    private static final int DEFAULT_PORTS_COUNT = 10;

    private final boolean mocked;
    private final String defaultImage;
    private final int basePort;
    private final int portsCount;

    private AcrConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.defaultImage = builder.defaultImage;
        this.basePort = builder.basePort;
        this.portsCount = builder.portsCount;
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
     * Returns whether no registry container is started; registries transition immediately to
     * {@code Succeeded} with a cosmetic {@code {name}.azurecr.io} login server.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the Docker image backing the registries.
     *
     * @return the Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the first port of the host port range of the registry instances.
     *
     * @return the base port
     */
    public int getBasePort() {
        return basePort;
    }

    /**
     * Returns the number of ports of the host port range of the registry instances, starting from {@link
     * #getBasePort()}.
     *
     * @return the number of ports
     */
    public int getPortsCount() {
        return portsCount;
    }

    /**
     * Returns the last port of the host port range of the registry instances.
     *
     * @return the maximum port
     */
    public int getMaxPort() {
        return basePort + portsCount - 1;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_ACR_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_ACR_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_ACR_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_AZ_SERVICES_ACR_BASE_PORT", String.valueOf(basePort));
            container.withEnv("FLOCI_AZ_SERVICES_ACR_MAX_PORT", String.valueOf(getMaxPort()));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link AcrConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AcrConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;
        private int basePort = DEFAULT_BASE_PORT;
        private int portsCount = DEFAULT_PORTS_COUNT;

        private Builder() {
            // Allow instantiation only via AcrConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AcrConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AcrConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.defaultImage = instance.defaultImage;
            this.basePort = instance.basePort;
            this.portsCount = instance.portsCount;
        }

        /**
         * Sets whether no registry container is started; registries transition immediately to
         * {@code Succeeded} with a cosmetic {@code {name}.azurecr.io} login server. Useful for tests
         * without Docker.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the Docker image backing the registries.
         *
         * @param defaultImage the Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the host port range the registry instances are published on. Floci itself allows
         * 5000-5099; this module defaults to 10 ports.
         *
         * @param basePort the first port of the range (default {@value DEFAULT_BASE_PORT})
         * @param amount   the number of ports in the range (default {@value DEFAULT_PORTS_COUNT})
         * @return this builder
         */
        public Builder portRange(int basePort, int amount) {
            this.basePort = basePort;
            this.portsCount = amount;
            return this;
        }

        /**
         * Creates an immutable {@link AcrConfig} from this builder.
         *
         * @return the Container Registry configuration
         */
        @Override
        public AcrConfig build() {
            return new AcrConfig(this);
        }
    }
}
