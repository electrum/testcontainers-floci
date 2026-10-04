package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure App Configuration of Floci Azure.
 *
 * <p>App Configuration is served under {@code /{account}-appconfig}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AppConfigConfig config = AppConfigConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class AppConfigConfig extends AbstractServiceConfig<AppConfigConfig.Builder> {

    private AppConfigConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_APP_CONFIG_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link AppConfigConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AppConfigConfig> {

        private Builder() {
            // Allow instantiation only via AppConfigConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AppConfigConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AppConfigConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link AppConfigConfig} from this builder.
         *
         * @return the App Configuration configuration
         */
        @Override
        public AppConfigConfig build() {
            return new AppConfigConfig(this);
        }
    }
}
