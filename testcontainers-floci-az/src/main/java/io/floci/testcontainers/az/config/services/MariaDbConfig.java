package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Database for MariaDB of Floci Azure.
 *
 * <p>Each server is backed by a MariaDB container that floci-az publishes on the Docker host.
 * In {@code mocked} mode servers are pure ARM state without a live endpoint.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MariaDbConfig config = MariaDbConfig.builder()
 *     .mocked(true)
 *     .image("mariadb:11.4")
 *     .startupTimeoutSeconds(120)
 *     .build();
 * }</pre>
 */
public class MariaDbConfig extends AbstractServiceConfig<MariaDbConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = false;
    private static final String DEFAULT_IMAGE = "mariadb:10.11";
    private static final int DEFAULT_STARTUP_TIMEOUT_SECONDS = 60;
    private static final int DEFAULT_DEFAULT_PORT = 0;

    private final boolean mocked;
    private final String image;
    private final int startupTimeoutSeconds;
    private final int defaultPort;

    private MariaDbConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.image = builder.image;
        this.startupTimeoutSeconds = builder.startupTimeoutSeconds;
        this.defaultPort = builder.defaultPort;
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
     * Returns whether no MariaDB container is started; servers transition immediately to
     * ready, but there is no live connection endpoint.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the Docker image backing each MariaDB server.
     *
     * @return the Docker image
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the maximum number of seconds to wait for a MariaDB container to become ready.
     *
     * @return the startup timeout in seconds
     */
    public int getStartupTimeoutSeconds() {
        return startupTimeoutSeconds;
    }

    /**
     * Returns the host port servers are published on. {@code 0} lets the OS pick a free port,
     * which is recommended when running multiple servers.
     *
     * @return the host port, or {@code 0} for a free port
     */
    public int getDefaultPort() {
        return defaultPort;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_MARIA_DB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_MARIA_DB_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_MARIA_DB_IMAGE", image);
            container.withEnv("FLOCI_AZ_SERVICES_MARIA_DB_STARTUP_TIMEOUT_SECONDS", String.valueOf(startupTimeoutSeconds));
            container.withEnv("FLOCI_AZ_SERVICES_MARIA_DB_DEFAULT_PORT", String.valueOf(defaultPort));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link MariaDbConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MariaDbConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String image = DEFAULT_IMAGE;
        private int startupTimeoutSeconds = DEFAULT_STARTUP_TIMEOUT_SECONDS;
        private int defaultPort = DEFAULT_DEFAULT_PORT;

        private Builder() {
            // Allow instantiation only via MariaDbConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MariaDbConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MariaDbConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.image = instance.image;
            this.startupTimeoutSeconds = instance.startupTimeoutSeconds;
            this.defaultPort = instance.defaultPort;
        }

        /**
         * Sets whether no MariaDB container is started; servers transition immediately to ready,
         * but there is no live connection endpoint. Useful for tests without Docker.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the Docker image backing each MariaDB server.
         *
         * @param image the Docker image (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * Sets the maximum number of seconds to wait for a MariaDB container to become ready.
         *
         * @param startupTimeoutSeconds the startup timeout in seconds (default {@value DEFAULT_STARTUP_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder startupTimeoutSeconds(int startupTimeoutSeconds) {
            this.startupTimeoutSeconds = startupTimeoutSeconds;
            return this;
        }

        /**
         * Sets the host port servers are published on. {@code 0} lets the OS pick a free port,
         * which is recommended when running multiple servers.
         *
         * @param defaultPort the host port, or {@code 0} for a free port (default {@value DEFAULT_DEFAULT_PORT})
         * @return this builder
         */
        public Builder defaultPort(int defaultPort) {
            this.defaultPort = defaultPort;
            return this;
        }

        /**
         * Creates an immutable {@link MariaDbConfig} from this builder.
         *
         * @return the MariaDB configuration
         */
        @Override
        public MariaDbConfig build() {
            return new MariaDbConfig(this);
        }
    }
}
