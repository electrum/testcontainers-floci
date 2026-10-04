package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Container Instances of Floci Azure.
 *
 * <p>In non-mocked mode, container groups run as real containers whose ports floci-az publishes on the
 * Docker host. By default the service is {@code mocked}: container groups are pure ARM state.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AciConfig config = AciConfig.builder()
 *     .mocked(false)
 *     .portRange(17500, 5)
 *     .build();
 * }</pre>
 */
public class AciConfig extends AbstractServiceConfig<AciConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = true;
    private static final int DEFAULT_BASE_PORT = 7500;
    private static final int DEFAULT_PORTS_COUNT = 10;

    private final boolean mocked;
    private final int basePort;
    private final int portsCount;

    private AciConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
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
     * Returns whether no Docker container is started; container groups transition immediately to
     * {@code Succeeded} with a synthetic IP and a {@code Running} instance view.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the first port of the host port range for published container-group ports.
     *
     * @return the base port
     */
    public int getBasePort() {
        return basePort;
    }

    /**
     * Returns the number of ports of the host port range for published container-group ports, starting from {@link
     * #getBasePort()}.
     *
     * @return the number of ports
     */
    public int getPortsCount() {
        return portsCount;
    }

    /**
     * Returns the last port of the host port range for published container-group ports.
     *
     * @return the maximum port
     */
    public int getMaxPort() {
        return basePort + portsCount - 1;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_ACI_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_ACI_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_ACI_BASE_PORT", String.valueOf(basePort));
            container.withEnv("FLOCI_AZ_SERVICES_ACI_MAX_PORT", String.valueOf(getMaxPort()));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link AciConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AciConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private int basePort = DEFAULT_BASE_PORT;
        private int portsCount = DEFAULT_PORTS_COUNT;

        private Builder() {
            // Allow instantiation only via AciConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AciConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AciConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.basePort = instance.basePort;
            this.portsCount = instance.portsCount;
        }

        /**
         * Sets whether no Docker container is started; container groups transition immediately to
         * {@code Succeeded} with a synthetic IP and a {@code Running} instance view.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the host port range published container-group ports are allocated from (non-mocked
         * mode). Floci itself allows 7500-7599; this module defaults to 10 ports.
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
         * Creates an immutable {@link AciConfig} from this builder.
         *
         * @return the Container Instances configuration
         */
        @Override
        public AciConfig build() {
            return new AciConfig(this);
        }
    }
}
