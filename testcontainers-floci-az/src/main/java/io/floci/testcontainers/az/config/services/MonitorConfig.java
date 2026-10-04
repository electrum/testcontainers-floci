package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Monitor / Log Analytics of Floci Azure.
 *
 * <p>Covers Microsoft.OperationalInsights and Microsoft.Insights resources.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MonitorConfig config = MonitorConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class MonitorConfig extends AbstractServiceConfig<MonitorConfig.Builder> {

    private MonitorConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_MONITOR_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link MonitorConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MonitorConfig> {

        private Builder() {
            // Allow instantiation only via MonitorConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MonitorConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MonitorConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link MonitorConfig} from this builder.
         *
         * @return the Monitor configuration
         */
        @Override
        public MonitorConfig build() {
            return new MonitorConfig(this);
        }
    }
}
