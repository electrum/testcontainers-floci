package io.floci.testcontainers.az.config.services;

import java.util.Optional;

/**
 * Configuration of one Cosmos DB API engine (e.g. MongoDB or Cassandra) of Floci Azure, see
 * {@link CosmosConfig}.
 *
 * <p>Instances are created via {@link CosmosConfig.Builder}, e.g.:
 * <pre>{@code
 * CosmosConfig config = CosmosConfig.builder()
 *     .mongodb(api -> api.enabled(true).image("mongo:8").port(27017))
 *     .build();
 * }</pre>
 */
public final class CosmosApiConfig {

    private static final boolean DEFAULT_ENABLED = false;

    private final boolean enabled;
    private final String image;
    private final Integer port;

    private CosmosApiConfig(Builder builder) {
        this.enabled = builder.enabled;
        this.image = builder.image;
        this.port = builder.port;
    }

    static CosmosApiConfig defaults() {
        return new Builder().build();
    }

    Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns whether this API engine is enabled.
     *
     * @return {@code true} if the API engine is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the Docker image overriding the engine's default image.
     *
     * @return the Docker image, or {@link Optional#empty()} if the engine's default image is used
     */
    public Optional<String> getImage() {
        return Optional.ofNullable(image);
    }

    /**
     * Returns the host port overriding the engine's default port.
     *
     * @return the host port, or {@link Optional#empty()} if the engine's default port is used
     */
    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    /**
     * Builder for {@link CosmosApiConfig}.
     */
    public static final class Builder {

        private boolean enabled = DEFAULT_ENABLED;
        private String image;
        private Integer port;

        private Builder() {
            // Allow instantiation only via CosmosConfig.Builder
        }

        private Builder(CosmosApiConfig instance) {
            this.enabled = instance.enabled;
            this.image = instance.image;
            this.port = instance.port;
        }

        /**
         * Enables or disables this API engine.
         *
         * @param enabled {@code true} to enable the API engine (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Overrides the Docker image of a Docker-backed API engine.
         *
         * @param image the Docker image, or {@code null} to use the engine's default image
         * @return this builder
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * Overrides the host port of the API engine.
         *
         * @param port the host port, or {@code null} to use the engine's default port
         * @return this builder
         */
        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        CosmosApiConfig build() {
            return new CosmosApiConfig(this);
        }
    }
}
