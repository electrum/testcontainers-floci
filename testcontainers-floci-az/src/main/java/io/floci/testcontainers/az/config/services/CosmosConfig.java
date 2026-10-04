package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.function.Consumer;

/**
 * Configuration for Azure Cosmos DB of Floci Azure.
 *
 * <p>The NoSQL API is always served in-process under {@code /{account}-cosmos}. In addition, Floci Azure
 * offers one engine per Cosmos DB API: NoSQL and Table run embedded, while MongoDB, PostgreSQL, Cassandra
 * and Gremlin are backed by containers that floci-az spawns (and therefore require the Docker socket). All
 * engines are disabled by default.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CosmosConfig config = CosmosConfig.builder()
 *     .engineStartup("eager")
 *     .mongodb(api -> api.enabled(true))
 *     .build();
 * }</pre>
 */
public class CosmosConfig extends AbstractServiceConfig<CosmosConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = false;
    private static final String DEFAULT_ENGINE_STARTUP = "on-demand";
    private static final String DEFAULT_DEFAULT_API = "nosql";

    private final boolean mocked;
    private final String engineStartup;
    private final String defaultApi;
    private final CosmosApiConfig nosql;
    private final CosmosApiConfig mongodb;
    private final CosmosApiConfig postgresql;
    private final CosmosApiConfig cassandra;
    private final CosmosApiConfig gremlin;
    private final CosmosApiConfig table;

    private CosmosConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.engineStartup = builder.engineStartup;
        this.defaultApi = builder.defaultApi;
        this.nosql = builder.nosql;
        this.mongodb = builder.mongodb;
        this.postgresql = builder.postgresql;
        this.cassandra = builder.cassandra;
        this.gremlin = builder.gremlin;
        this.table = builder.table;
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
     * Returns whether no Cosmos DB engine containers are started for any API, which is equivalent to an
     * {@linkplain #getEngineStartup() engine startup mode} of {@code disabled}. The in-process NoSQL and
     * Table paths are unaffected.
     *
     * @return {@code true} if engine containers are disabled
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns when the enabled API engines are started: {@code on-demand}, {@code eager} or
     * {@code disabled}.
     *
     * @return the engine startup mode
     */
    public String getEngineStartup() {
        return engineStartup;
    }

    /**
     * Returns the default Cosmos DB API (e.g. {@code nosql}).
     *
     * @return the default API
     */
    public String getDefaultApi() {
        return defaultApi;
    }

    /**
     * Returns the configuration of the embedded NoSQL API engine.
     *
     * @return the NoSQL engine configuration
     */
    public CosmosApiConfig getNosql() {
        return nosql;
    }

    /**
     * Returns the configuration of the Docker-backed MongoDB API engine.
     *
     * @return the MongoDB engine configuration
     */
    public CosmosApiConfig getMongodb() {
        return mongodb;
    }

    /**
     * Returns the configuration of the Docker-backed PostgreSQL API engine.
     *
     * @return the PostgreSQL engine configuration
     */
    public CosmosApiConfig getPostgresql() {
        return postgresql;
    }

    /**
     * Returns the configuration of the Docker-backed Cassandra API engine.
     *
     * @return the Cassandra engine configuration
     */
    public CosmosApiConfig getCassandra() {
        return cassandra;
    }

    /**
     * Returns the configuration of the Docker-backed Gremlin API engine.
     *
     * @return the Gremlin engine configuration
     */
    public CosmosApiConfig getGremlin() {
        return gremlin;
    }

    /**
     * Returns the configuration of the embedded Table API engine.
     *
     * @return the Table engine configuration
     */
    public CosmosApiConfig getTable() {
        return table;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_COSMOS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_COSMOS_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_COSMOS_ENGINES_STARTUP", engineStartup);
            container.withEnv("FLOCI_AZ_SERVICES_COSMOS_ENGINES_DEFAULT_API", defaultApi);
            applyApiEnvVarsToContainer(container, "NOSQL", nosql);
            applyApiEnvVarsToContainer(container, "MONGODB", mongodb);
            applyApiEnvVarsToContainer(container, "POSTGRESQL", postgresql);
            applyApiEnvVarsToContainer(container, "CASSANDRA", cassandra);
            applyApiEnvVarsToContainer(container, "GREMLIN", gremlin);
            applyApiEnvVarsToContainer(container, "TABLE", table);
        }
    }

    private static void applyApiEnvVarsToContainer(Container<?> container, String api, CosmosApiConfig config) {
        String prefix = "FLOCI_AZ_SERVICES_COSMOS_ENGINES_" + api + "_";
        container.withEnv(prefix + "ENABLED", String.valueOf(config.isEnabled()));
        config.getImage().ifPresent(image -> container.withEnv(prefix + "IMAGE", image));
        config.getPort().ifPresent(port -> container.withEnv(prefix + "PORT", String.valueOf(port)));
    }

    /**
     * Returns {@code true} while enabled, not mocked, the engine startup is not {@code disabled} and at least
     * one of the Docker-backed API engines (MongoDB, PostgreSQL, Cassandra, Gremlin) is enabled.
     */
    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked && !"disabled".equals(engineStartup)
                && (mongodb.isEnabled() || postgresql.isEnabled() || cassandra.isEnabled() || gremlin.isEnabled());
    }

    /**
     * Builder for {@link CosmosConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CosmosConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String engineStartup = DEFAULT_ENGINE_STARTUP;
        private String defaultApi = DEFAULT_DEFAULT_API;
        private CosmosApiConfig nosql = CosmosApiConfig.defaults();
        private CosmosApiConfig mongodb = CosmosApiConfig.defaults();
        private CosmosApiConfig postgresql = CosmosApiConfig.defaults();
        private CosmosApiConfig cassandra = CosmosApiConfig.defaults();
        private CosmosApiConfig gremlin = CosmosApiConfig.defaults();
        private CosmosApiConfig table = CosmosApiConfig.defaults();

        private Builder() {
            // Allow instantiation only via CosmosConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CosmosConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CosmosConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.engineStartup = instance.engineStartup;
            this.defaultApi = instance.defaultApi;
            this.nosql = instance.nosql;
            this.mongodb = instance.mongodb;
            this.postgresql = instance.postgresql;
            this.cassandra = instance.cassandra;
            this.gremlin = instance.gremlin;
            this.table = instance.table;
        }

        /**
         * Sets whether no Cosmos DB engine containers are started for any API, which is equivalent to an
         * {@linkplain #engineStartup(String) engine startup mode} of {@code disabled}. The in-process NoSQL
         * and Table paths are unaffected. Useful for tests without Docker.
         *
         * @param mocked {@code true} to disable all engine containers (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets when the enabled API engines are started: {@code on-demand} (on first use), {@code eager}
         * (with the emulator) or {@code disabled}.
         *
         * @param engineStartup the engine startup mode (default {@value DEFAULT_ENGINE_STARTUP})
         * @return this builder
         */
        public Builder engineStartup(String engineStartup) {
            this.engineStartup = engineStartup;
            return this;
        }

        /**
         * Sets the default Cosmos DB API.
         *
         * @param defaultApi the default API (default {@value DEFAULT_DEFAULT_API})
         * @return this builder
         */
        public Builder defaultApi(String defaultApi) {
            this.defaultApi = defaultApi;
            return this;
        }

        /**
         * Configures the embedded NoSQL API engine.
         *
         * @param configurer a consumer that receives a {@link CosmosApiConfig.Builder} to modify
         * @return this builder
         */
        public Builder nosql(Consumer<CosmosApiConfig.Builder> configurer) {
            this.nosql = configure(nosql, configurer);
            return this;
        }

        /**
         * Configures the Docker-backed MongoDB API engine.
         *
         * @param configurer a consumer that receives a {@link CosmosApiConfig.Builder} to modify
         * @return this builder
         */
        public Builder mongodb(Consumer<CosmosApiConfig.Builder> configurer) {
            this.mongodb = configure(mongodb, configurer);
            return this;
        }

        /**
         * Configures the Docker-backed PostgreSQL API engine.
         *
         * @param configurer a consumer that receives a {@link CosmosApiConfig.Builder} to modify
         * @return this builder
         */
        public Builder postgresql(Consumer<CosmosApiConfig.Builder> configurer) {
            this.postgresql = configure(postgresql, configurer);
            return this;
        }

        /**
         * Configures the Docker-backed Cassandra API engine.
         *
         * @param configurer a consumer that receives a {@link CosmosApiConfig.Builder} to modify
         * @return this builder
         */
        public Builder cassandra(Consumer<CosmosApiConfig.Builder> configurer) {
            this.cassandra = configure(cassandra, configurer);
            return this;
        }

        /**
         * Configures the Docker-backed Gremlin API engine.
         *
         * @param configurer a consumer that receives a {@link CosmosApiConfig.Builder} to modify
         * @return this builder
         */
        public Builder gremlin(Consumer<CosmosApiConfig.Builder> configurer) {
            this.gremlin = configure(gremlin, configurer);
            return this;
        }

        /**
         * Configures the embedded Table API engine.
         *
         * @param configurer a consumer that receives a {@link CosmosApiConfig.Builder} to modify
         * @return this builder
         */
        public Builder table(Consumer<CosmosApiConfig.Builder> configurer) {
            this.table = configure(table, configurer);
            return this;
        }

        private static CosmosApiConfig configure(CosmosApiConfig current, Consumer<CosmosApiConfig.Builder> configurer) {
            CosmosApiConfig.Builder builder = current.toBuilder();
            configurer.accept(builder);
            return builder.build();
        }

        /**
         * Creates an immutable {@link CosmosConfig} from this builder.
         *
         * @return the Cosmos DB configuration
         */
        @Override
        public CosmosConfig build() {
            return new CosmosConfig(this);
        }
    }
}
