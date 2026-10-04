package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Kubernetes Service (AKS) of Floci Azure.
 *
 * <p>Each cluster is backed by a k3s container whose API server floci-az publishes on the Docker host.
 * In {@code mocked} mode clusters get a synthetic kubeconfig instead.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AksConfig config = AksConfig.builder()
 *     .mocked(true)
 *     .defaultImage("rancher/k3s:v1.31.4-k3s1")
 *     .apiServerPortRange(16443, 5)
 *     .build();
 * }</pre>
 */
public class AksConfig extends AbstractServiceConfig<AksConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = false;
    private static final String DEFAULT_DEFAULT_IMAGE = "rancher/k3s:latest";
    private static final int DEFAULT_API_SERVER_BASE_PORT = 6443;
    private static final int DEFAULT_API_SERVER_PORTS_COUNT = 10;
    private static final boolean DEFAULT_KEEP_RUNNING_ON_SHUTDOWN = false;

    private final boolean mocked;
    private final String defaultImage;
    private final int apiServerBasePort;
    private final int apiServerPortsCount;
    private final boolean keepRunningOnShutdown;

    private AksConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.defaultImage = builder.defaultImage;
        this.apiServerBasePort = builder.apiServerBasePort;
        this.apiServerPortsCount = builder.apiServerPortsCount;
        this.keepRunningOnShutdown = builder.keepRunningOnShutdown;
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
     * Returns whether no k3s container is started; clusters transition immediately to
     * {@code Succeeded} with a synthetic kubeconfig.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the Docker image for the k3s container.
     *
     * @return the Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the first port of the host port range of the k3s API servers.
     *
     * @return the base port
     */
    public int getApiServerBasePort() {
        return apiServerBasePort;
    }

    /**
     * Returns the number of ports of the host port range of the k3s API servers, starting from {@link #getApiServerBasePort()}.
     *
     * @return the number of ports
     */
    public int getApiServerPortsCount() {
        return apiServerPortsCount;
    }

    /**
     * Returns the last port of the host port range of the k3s API servers.
     *
     * @return the maximum port
     */
    public int getApiServerMaxPort() {
        return apiServerBasePort + apiServerPortsCount - 1;
    }

    /**
     * Returns whether k3s containers are left running when floci-az shuts down.
     *
     * @return {@code true} if k3s containers survive a shutdown
     */
    public boolean isKeepRunningOnShutdown() {
        return keepRunningOnShutdown;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_AKS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_AKS_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_AKS_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_AZ_SERVICES_AKS_API_SERVER_BASE_PORT", String.valueOf(apiServerBasePort));
            container.withEnv("FLOCI_AZ_SERVICES_AKS_API_SERVER_MAX_PORT", String.valueOf(getApiServerMaxPort()));
            container.withEnv("FLOCI_AZ_SERVICES_AKS_KEEP_RUNNING_ON_SHUTDOWN", String.valueOf(keepRunningOnShutdown));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link AksConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AksConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;
        private int apiServerBasePort = DEFAULT_API_SERVER_BASE_PORT;
        private int apiServerPortsCount = DEFAULT_API_SERVER_PORTS_COUNT;
        private boolean keepRunningOnShutdown = DEFAULT_KEEP_RUNNING_ON_SHUTDOWN;

        private Builder() {
            // Allow instantiation only via AksConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AksConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AksConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.defaultImage = instance.defaultImage;
            this.apiServerBasePort = instance.apiServerBasePort;
            this.apiServerPortsCount = instance.apiServerPortsCount;
            this.keepRunningOnShutdown = instance.keepRunningOnShutdown;
        }

        /**
         * Sets whether no k3s container is started; clusters transition immediately to {@code Succeeded}
         * with a synthetic kubeconfig. Useful for tests without Docker.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the Docker image for the k3s container.
         *
         * @param defaultImage the Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the host port range the k3s API servers are published on. Floci itself allows
         * 6443-7443; this module defaults to 10 ports, i.e. 10 concurrent clusters.
         *
         * @param basePort the first port of the range (default {@value DEFAULT_API_SERVER_BASE_PORT})
         * @param amount   the number of ports in the range (default {@value DEFAULT_API_SERVER_PORTS_COUNT})
         * @return this builder
         */
        public Builder apiServerPortRange(int basePort, int amount) {
            this.apiServerBasePort = basePort;
            this.apiServerPortsCount = amount;
            return this;
        }

        /**
         * Sets whether k3s containers are left running when floci-az shuts down.
         *
         * @param keepRunningOnShutdown {@code true} to keep k3s containers running (default {@value DEFAULT_KEEP_RUNNING_ON_SHUTDOWN})
         * @return this builder
         */
        public Builder keepRunningOnShutdown(boolean keepRunningOnShutdown) {
            this.keepRunningOnShutdown = keepRunningOnShutdown;
            return this;
        }

        /**
         * Creates an immutable {@link AksConfig} from this builder.
         *
         * @return the AKS configuration
         */
        @Override
        public AksConfig build() {
            return new AksConfig(this);
        }
    }
}
