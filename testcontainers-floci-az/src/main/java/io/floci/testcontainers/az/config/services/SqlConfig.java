package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Locale;
import java.util.Optional;

/**
 * Configuration for Azure SQL Database of Floci Azure.
 *
 * <p>Servers, databases and firewall rules are always available via ARM. Whether a data plane (SQL Server
 * container) backs the servers is decided by the {@linkplain #getDataPlaneProvider() data plane provider}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * SqlConfig config = SqlConfig.builder()
 *     .dataPlaneProvider(SqlConfig.SqlDataPlaneProvider.MANAGED)
 *     .acceptEula("Y")
 *     .image("mcr.microsoft.com/mssql/server:2022-latest")
 *     .build();
 * }</pre>
 */
public class SqlConfig extends AbstractServiceConfig<SqlConfig.Builder> {

    private static final String DEFAULT_ACCEPT_EULA = "N";
    private static final String DEFAULT_IMAGE = "mcr.microsoft.com/mssql/server:2025-latest";
    private static final String DEFAULT_SA_PASSWORD = "FlociAz_Strong123!";
    private static final int DEFAULT_STARTUP_TIMEOUT_SECONDS = 60;
    private static final int DEFAULT_DEFAULT_PORT = 0;

    private final SqlDataPlaneProvider dataPlaneProvider;
    private final String acceptEula;
    private final String image;
    private final String saPassword;
    private final int startupTimeoutSeconds;
    private final int defaultPort;

    private SqlConfig(Builder builder) {
        super(builder);
        this.dataPlaneProvider = builder.dataPlaneProvider;
        this.acceptEula = builder.acceptEula;
        this.image = builder.image;
        this.saPassword = builder.saPassword;
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
     * Returns the explicitly configured data plane provider of the Azure SQL servers. When empty, Floci Azure
     * decides based on {@link #getAcceptEula()}: an accepted EULA selects {@link SqlDataPlaneProvider#MANAGED},
     * otherwise {@link SqlDataPlaneProvider#NONE} (control plane only).
     *
     * @return the data plane provider, or {@link Optional#empty()} if not configured
     */
    public Optional<SqlDataPlaneProvider> getDataPlaneProvider() {
        return Optional.ofNullable(dataPlaneProvider);
    }

    /**
     * Returns whether the Microsoft SQL Server EULA is accepted ({@code Y}) or not ({@code N}).
     *
     * @return {@code Y} if the EULA is accepted
     */
    public String getAcceptEula() {
        return acceptEula;
    }

    /**
     * Returns the Docker image for the SQL Server containers.
     *
     * @return the Docker image
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the SA (system administrator) password of the SQL Server containers.
     *
     * @return the SA password
     */
    public String getSaPassword() {
        return saPassword;
    }

    /**
     * Returns the maximum number of seconds to wait for a SQL Server container to become ready.
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
        container.withEnv("FLOCI_AZ_SERVICES_SQL_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_SQL_ACCEPT_EULA", acceptEula);
            container.withEnv("FLOCI_AZ_SERVICES_SQL_IMAGE", image);
            container.withEnv("FLOCI_AZ_SERVICES_SQL_SA_PASSWORD", saPassword);
            container.withEnv("FLOCI_AZ_SERVICES_SQL_STARTUP_TIMEOUT_SECONDS", String.valueOf(startupTimeoutSeconds));
            container.withEnv("FLOCI_AZ_SERVICES_SQL_DEFAULT_PORT", String.valueOf(defaultPort));

            if (dataPlaneProvider != null) {
                container.withEnv("FLOCI_AZ_SERVICES_SQL_DATA_PLANE_PROVIDER", dataPlaneProvider.name().toLowerCase(Locale.ROOT));
            }
        }
    }

    /**
     * Returns the data plane provider Floci Azure effectively uses: the {@linkplain #getDataPlaneProvider()
     * configured one}, or {@link SqlDataPlaneProvider#MANAGED} if the EULA is accepted and
     * {@link SqlDataPlaneProvider#NONE} otherwise.
     *
     * @return the effective data plane provider
     */
    public SqlDataPlaneProvider getEffectiveDataPlaneProvider() {
        if (dataPlaneProvider != null) {
            return dataPlaneProvider;
        }
        return "Y".equalsIgnoreCase(acceptEula) ? SqlDataPlaneProvider.MANAGED : SqlDataPlaneProvider.NONE;
    }

    /**
     * Returns {@code true} while enabled with the {@linkplain #getEffectiveDataPlaneProvider() effective data plane
     * provider} {@link SqlDataPlaneProvider#MANAGED}, which runs SQL Server in sibling containers.
     */
    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && getEffectiveDataPlaneProvider() == SqlDataPlaneProvider.MANAGED;
    }

    /**
     * Data plane providers of the Azure SQL servers.
     */
    public enum SqlDataPlaneProvider {
        /**
         * Control plane only: servers and databases exist as ARM resources without a live endpoint.
         */
        NONE,
        /**
         * Each server is backed by a SQL Server container spawned by Floci Azure.
         */
        MANAGED,
        /**
         * Servers point at an externally provided SQL Server.
         */
        EXTERNAL
    }

    /**
     * Builder for {@link SqlConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, SqlConfig> {

        private SqlDataPlaneProvider dataPlaneProvider;
        private String acceptEula = DEFAULT_ACCEPT_EULA;
        private String image = DEFAULT_IMAGE;
        private String saPassword = DEFAULT_SA_PASSWORD;
        private int startupTimeoutSeconds = DEFAULT_STARTUP_TIMEOUT_SECONDS;
        private int defaultPort = DEFAULT_DEFAULT_PORT;

        private Builder() {
            // Allow instantiation only via SqlConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link SqlConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(SqlConfig instance) {
            super(instance);
            this.dataPlaneProvider = instance.dataPlaneProvider;
            this.acceptEula = instance.acceptEula;
            this.image = instance.image;
            this.saPassword = instance.saPassword;
            this.startupTimeoutSeconds = instance.startupTimeoutSeconds;
            this.defaultPort = instance.defaultPort;
        }

        /**
         * Sets the data plane provider of the Azure SQL servers: {@link SqlDataPlaneProvider#NONE} (control
         * plane only), {@link SqlDataPlaneProvider#MANAGED} (SQL Server containers spawned by Floci Azure) or
         * {@link SqlDataPlaneProvider#EXTERNAL}. When not set, Floci Azure selects
         * {@link SqlDataPlaneProvider#MANAGED} if the {@linkplain #acceptEula(String) EULA is accepted} and
         * {@link SqlDataPlaneProvider#NONE} otherwise.
         *
         * @param dataPlaneProvider the data plane provider ({@code null} to unset)
         * @return this builder
         */
        public Builder dataPlaneProvider(SqlDataPlaneProvider dataPlaneProvider) {
            this.dataPlaneProvider = dataPlaneProvider;
            return this;
        }

        /**
         * Sets whether the Microsoft SQL Server EULA is accepted. Must be {@code Y} to run SQL Server
         * containers. Without an explicit {@link #dataPlaneProvider(SqlDataPlaneProvider) data plane
         * provider}, accepting the EULA selects the {@code managed} data plane.
         *
         * @param acceptEula {@code Y} to accept the EULA (default {@value DEFAULT_ACCEPT_EULA})
         * @return this builder
         */
        public Builder acceptEula(String acceptEula) {
            this.acceptEula = acceptEula;
            return this;
        }

        /**
         * Sets the Docker image for the SQL Server containers.
         *
         * @param image the Docker image (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * Sets the SA (system administrator) password of the SQL Server containers. Must meet the SQL
         * Server complexity requirements (at least 8 characters with upper and lower case letters,
         * digits and special characters).
         *
         * @param saPassword the SA password (default {@value DEFAULT_SA_PASSWORD})
         * @return this builder
         */
        public Builder saPassword(String saPassword) {
            this.saPassword = saPassword;
            return this;
        }

        /**
         * Sets the maximum number of seconds to wait for a SQL Server container to become ready.
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
         * Creates an immutable {@link SqlConfig} from this builder.
         *
         * @return the Azure SQL configuration
         */
        @Override
        public SqlConfig build() {
            return new SqlConfig(this);
        }
    }
}
