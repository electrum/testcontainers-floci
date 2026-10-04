package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Container Apps of Floci Azure.
 *
 * <p>Managed environments and container apps (Microsoft.App). By default the service is {@code mocked}:
 * ARM state and revisions are kept without starting application containers.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ContainerAppsConfig config = ContainerAppsConfig.builder()
 *     .mocked(false)
 *     .dnsSuffix("apps.example.test")
 *     .ingressTimeoutSeconds(30)
 *     .build();
 * }</pre>
 */
public class ContainerAppsConfig extends AbstractServiceConfig<ContainerAppsConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = true;
    private static final String DEFAULT_DNS_SUFFIX = "azurecontainerapps.io";
    private static final int DEFAULT_INGRESS_TIMEOUT_SECONDS = 60;

    private final boolean mocked;
    private final String dnsSuffix;
    private final int ingressTimeoutSeconds;

    private ContainerAppsConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.dnsSuffix = builder.dnsSuffix;
        this.ingressTimeoutSeconds = builder.ingressTimeoutSeconds;
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
     * Returns whether ARM state and revisions are kept without starting application containers.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the DNS suffix used for the emulated environment, app and revision FQDNs.
     *
     * @return the DNS suffix
     */
    public String getDnsSuffix() {
        return dnsSuffix;
    }

    /**
     * Returns the timeout in seconds for proxied ingress requests to application containers.
     *
     * @return the ingress timeout in seconds
     */
    public int getIngressTimeoutSeconds() {
        return ingressTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_CONTAINER_APPS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_CONTAINER_APPS_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_CONTAINER_APPS_DNS_SUFFIX", dnsSuffix);
            container.withEnv("FLOCI_AZ_SERVICES_CONTAINER_APPS_INGRESS_TIMEOUT_SECONDS", String.valueOf(ingressTimeoutSeconds));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link ContainerAppsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ContainerAppsConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String dnsSuffix = DEFAULT_DNS_SUFFIX;
        private int ingressTimeoutSeconds = DEFAULT_INGRESS_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via ContainerAppsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ContainerAppsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ContainerAppsConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.dnsSuffix = instance.dnsSuffix;
            this.ingressTimeoutSeconds = instance.ingressTimeoutSeconds;
        }

        /**
         * Sets whether ARM state and revisions are kept without starting application containers.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the DNS suffix used for the emulated environment, app and revision FQDNs.
         *
         * @param dnsSuffix the DNS suffix (default {@value DEFAULT_DNS_SUFFIX})
         * @return this builder
         */
        public Builder dnsSuffix(String dnsSuffix) {
            this.dnsSuffix = dnsSuffix;
            return this;
        }

        /**
         * Sets the timeout in seconds for proxied ingress requests to application containers.
         *
         * @param ingressTimeoutSeconds the ingress timeout in seconds (default {@value DEFAULT_INGRESS_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder ingressTimeoutSeconds(int ingressTimeoutSeconds) {
            this.ingressTimeoutSeconds = ingressTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link ContainerAppsConfig} from this builder.
         *
         * @return the Container Apps configuration
         */
        @Override
        public ContainerAppsConfig build() {
            return new ContainerAppsConfig(this);
        }
    }
}
