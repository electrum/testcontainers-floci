package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure SignalR Service of Floci Azure.
 *
 * <p>SignalR runs in default mode over WebSockets, served under {@code /server/} and {@code /client/}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SignalRConfig config = SignalRConfig.builder()
 *     .accessKey("bXktc2lnbmFsci1rZXk=")
 *     .build();
 * }</pre>
 */
public class SignalRConfig extends AbstractServiceConfig<SignalRConfig.Builder> {

    private static final String DEFAULT_ACCESS_KEY = "bG9jYWwtc2lnbmFsci1kZXZlbG9wbWVudC1rZXk=";

    private final String accessKey;

    private SignalRConfig(Builder builder) {
        super(builder);
        this.accessKey = builder.accessKey;
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
     * Returns the base64-encoded signing key shared with the Azure SignalR SDK.
     *
     * @return the base64-encoded access key
     */
    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_SIGNALR_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_SIGNALR_ACCESS_KEY", accessKey);
        }
    }

    /**
     * Builder for {@link SignalRConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SignalRConfig> {

        private String accessKey = DEFAULT_ACCESS_KEY;

        private Builder() {
            // Allow instantiation only via SignalRConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SignalRConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SignalRConfig instance) {
            super(instance);
            this.accessKey = instance.accessKey;
        }

        /**
         * Sets the base64-encoded signing key shared with the Azure SignalR SDK.
         *
         * @param accessKey the base64-encoded access key (default {@value DEFAULT_ACCESS_KEY})
         * @return this builder
         */
        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        /**
         * Creates an immutable {@link SignalRConfig} from this builder.
         *
         * @return the SignalR configuration
         */
        @Override
        public SignalRConfig build() {
            return new SignalRConfig(this);
        }
    }
}
