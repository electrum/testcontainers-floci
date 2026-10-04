package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Event Grid of Floci Azure.
 *
 * <p>Custom topics and webhook event subscriptions: events published to a topic endpoint are fanned out
 * to the subscriber webhooks.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EventGridConfig config = EventGridConfig.builder()
 *     .defaultRegion("westeurope")
 *     .maxDeliveryAttempts(5)
 *     .build();
 * }</pre>
 */
public class EventGridConfig extends AbstractServiceConfig<EventGridConfig.Builder> {

    private static final String DEFAULT_DEFAULT_REGION = "eastus";
    private static final int DEFAULT_MAX_DELIVERY_ATTEMPTS = 30;

    private final String defaultRegion;
    private final int maxDeliveryAttempts;

    private EventGridConfig(Builder builder) {
        super(builder);
        this.defaultRegion = builder.defaultRegion;
        this.maxDeliveryAttempts = builder.maxDeliveryAttempts;
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
     * Returns the region label used in the topic endpoint host returned by ARM.
     *
     * @return the default region
     */
    public String getDefaultRegion() {
        return defaultRegion;
    }

    /**
     * Returns the maximum number of delivery attempts for subscriptions without their own retry
     * policy.
     *
     * @return the maximum number of delivery attempts
     */
    public int getMaxDeliveryAttempts() {
        return maxDeliveryAttempts;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_EVENT_GRID_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_GRID_DEFAULT_REGION", defaultRegion);
            container.withEnv("FLOCI_AZ_SERVICES_EVENT_GRID_MAX_DELIVERY_ATTEMPTS", String.valueOf(maxDeliveryAttempts));
        }
    }

    /**
     * Builder for {@link EventGridConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EventGridConfig> {

        private String defaultRegion = DEFAULT_DEFAULT_REGION;
        private int maxDeliveryAttempts = DEFAULT_MAX_DELIVERY_ATTEMPTS;

        private Builder() {
            // Allow instantiation only via EventGridConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EventGridConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EventGridConfig instance) {
            super(instance);
            this.defaultRegion = instance.defaultRegion;
            this.maxDeliveryAttempts = instance.maxDeliveryAttempts;
        }

        /**
         * Sets the region label used in the topic endpoint host returned by ARM.
         *
         * @param defaultRegion the default region (default {@value DEFAULT_DEFAULT_REGION})
         * @return this builder
         */
        public Builder defaultRegion(String defaultRegion) {
            this.defaultRegion = defaultRegion;
            return this;
        }

        /**
         * Sets the maximum number of delivery attempts for subscriptions without their own retry
         * policy.
         *
         * @param maxDeliveryAttempts the maximum number of delivery attempts (default {@value DEFAULT_MAX_DELIVERY_ATTEMPTS})
         * @return this builder
         */
        public Builder maxDeliveryAttempts(int maxDeliveryAttempts) {
            this.maxDeliveryAttempts = maxDeliveryAttempts;
            return this;
        }

        /**
         * Creates an immutable {@link EventGridConfig} from this builder.
         *
         * @return the Event Grid configuration
         */
        @Override
        public EventGridConfig build() {
            return new EventGridConfig(this);
        }
    }
}
