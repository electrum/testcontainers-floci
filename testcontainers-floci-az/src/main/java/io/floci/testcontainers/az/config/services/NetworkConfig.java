package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Virtual Network of Floci Azure.
 *
 * <p>Covers Microsoft.Network: virtual networks, subnets, NICs, public IPs, NSGs, DNS zones and
 * private endpoints.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * NetworkConfig config = NetworkConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class NetworkConfig extends AbstractServiceConfig<NetworkConfig.Builder> {

    private NetworkConfig(Builder builder) {
        super(builder);
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_NETWORK_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link NetworkConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, NetworkConfig> {

        private Builder() {
            // Allow instantiation only via NetworkConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link NetworkConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(NetworkConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link NetworkConfig} from this builder.
         *
         * @return the Virtual Network configuration
         */
        @Override
        public NetworkConfig build() {
            return new NetworkConfig(this);
        }
    }
}
