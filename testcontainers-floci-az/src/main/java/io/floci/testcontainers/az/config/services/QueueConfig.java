package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Queue Storage of Floci Azure.
 *
 * <p>Queue Storage is served under {@code /{account}-queue} (see
 * {@code FlociAzContainer#getQueueEndpoint()}).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * QueueConfig config = QueueConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class QueueConfig extends AbstractServiceConfig<QueueConfig.Builder> {

    private QueueConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_QUEUE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link QueueConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, QueueConfig> {

        private Builder() {
            // Allow instantiation only via QueueConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link QueueConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(QueueConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link QueueConfig} from this builder.
         *
         * @return the Queue Storage configuration
         */
        @Override
        public QueueConfig build() {
            return new QueueConfig(this);
        }
    }
}
