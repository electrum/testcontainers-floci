package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Communication Services Email of Floci Azure.
 *
 * <p>Covers Microsoft.Communication email sending.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EmailConfig config = EmailConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class EmailConfig extends AbstractServiceConfig<EmailConfig.Builder> {

    private EmailConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_EMAIL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link EmailConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EmailConfig> {

        private Builder() {
            // Allow instantiation only via EmailConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EmailConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EmailConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link EmailConfig} from this builder.
         *
         * @return the Email configuration
         */
        @Override
        public EmailConfig build() {
            return new EmailConfig(this);
        }
    }
}
