package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure API Management of Floci Azure.
 *
 * <p>API Management resources are managed via ARM; the gateway routes under
 * {@code /{account}-apim/{serviceName}/...}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ApimConfig config = ApimConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class ApimConfig extends AbstractServiceConfig<ApimConfig.Builder> {

    private ApimConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_APIM_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ApimConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ApimConfig> {

        private Builder() {
            // Allow instantiation only via ApimConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ApimConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ApimConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ApimConfig} from this builder.
         *
         * @return the API Management configuration
         */
        @Override
        public ApimConfig build() {
            return new ApimConfig(this);
        }
    }
}
