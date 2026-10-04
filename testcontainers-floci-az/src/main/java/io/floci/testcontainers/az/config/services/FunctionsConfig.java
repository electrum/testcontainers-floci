package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Azure Functions of Floci Azure.
 *
 * <p>Deployed functions are executed in runtime containers spawned by floci-az, which requires the
 * Docker socket unless the service is {@code mocked}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * FunctionsConfig config = FunctionsConfig.builder()
 *     .mocked(true)
 *     .codePath("/app/data/functions")
 *     .ephemeral(true)
 *     .build();
 * }</pre>
 */
public class FunctionsConfig extends AbstractServiceConfig<FunctionsConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = false;
    private static final boolean DEFAULT_EPHEMERAL = false;
    private static final int DEFAULT_CONTAINER_IDLE_TIMEOUT_SECONDS = 300;

    private final boolean mocked;
    private final String codePath;
    private final boolean ephemeral;
    private final int containerIdleTimeoutSeconds;
    private final String dockerHostOverride;

    private FunctionsConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.codePath = builder.codePath;
        this.ephemeral = builder.ephemeral;
        this.containerIdleTimeoutSeconds = builder.containerIdleTimeoutSeconds;
        this.dockerHostOverride = builder.dockerHostOverride;
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
     * Returns whether no Functions runtime container is started. The management plane still works,
     * but invocations return a synthetic 200 stub instead of executing user code.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the directory inside the container where deployed function code is stored. When
     * empty, floci-az uses {@code ${user.home}/.floci-az/functions}.
     *
     * @return the the function code directory, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getCodePath() {
        return Optional.ofNullable(codePath);
    }

    /**
     * Returns whether each invocation gets a fresh container instead of reusing a warm one.
     *
     * @return {@code true} if containers are not reused
     */
    public boolean isEphemeral() {
        return ephemeral;
    }

    /**
     * Returns the number of seconds after which idle warm containers are evicted.
     * {@code 0} disables eviction.
     *
     * @return the idle timeout in seconds
     */
    public int getContainerIdleTimeoutSeconds() {
        return containerIdleTimeoutSeconds;
    }

    /**
     * Returns the hostname function containers use to reach floci-az. When empty, floci-az
     * detects it automatically.
     *
     * @return the the hostname override, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDockerHostOverride() {
        return Optional.ofNullable(dockerHostOverride);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_FUNCTIONS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_FUNCTIONS_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_FUNCTIONS_EPHEMERAL", String.valueOf(ephemeral));
            container.withEnv("FLOCI_AZ_SERVICES_FUNCTIONS_CONTAINER_IDLE_TIMEOUT_SECONDS", String.valueOf(containerIdleTimeoutSeconds));

            if (codePath != null) {
                container.withEnv("FLOCI_AZ_SERVICES_FUNCTIONS_CODE_PATH", codePath);
            }

            if (dockerHostOverride != null) {
                container.withEnv("FLOCI_AZ_SERVICES_FUNCTIONS_DOCKER_HOST_OVERRIDE", dockerHostOverride);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link FunctionsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, FunctionsConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String codePath;
        private boolean ephemeral = DEFAULT_EPHEMERAL;
        private int containerIdleTimeoutSeconds = DEFAULT_CONTAINER_IDLE_TIMEOUT_SECONDS;
        private String dockerHostOverride;

        private Builder() {
            // Allow instantiation only via FunctionsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link FunctionsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(FunctionsConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.codePath = instance.codePath;
            this.ephemeral = instance.ephemeral;
            this.containerIdleTimeoutSeconds = instance.containerIdleTimeoutSeconds;
            this.dockerHostOverride = instance.dockerHostOverride;
        }

        /**
         * Sets whether no Functions runtime container is started. The management plane still works,
         * but invocations return a synthetic 200 stub instead of executing user code. Useful for tests
         * without Docker.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the directory inside the container where deployed function code is stored. When
         * not set, floci-az uses {@code ${user.home}/.floci-az/functions}.
         *
         * @param codePath the function code directory ({@code null} to unset)
         * @return this builder
         */
        public Builder codePath(String codePath) {
            this.codePath = codePath;
            return this;
        }

        /**
         * Sets whether each invocation gets a fresh container instead of reusing a warm one.
         *
         * @param ephemeral {@code true} to disable warm container reuse (default {@value DEFAULT_EPHEMERAL})
         * @return this builder
         */
        public Builder ephemeral(boolean ephemeral) {
            this.ephemeral = ephemeral;
            return this;
        }

        /**
         * Sets the number of seconds after which idle warm containers are evicted.
         * {@code 0} disables eviction.
         *
         * @param containerIdleTimeoutSeconds the idle timeout in seconds (default {@value DEFAULT_CONTAINER_IDLE_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder containerIdleTimeoutSeconds(int containerIdleTimeoutSeconds) {
            this.containerIdleTimeoutSeconds = containerIdleTimeoutSeconds;
            return this;
        }

        /**
         * Overrides the hostname function containers use to reach floci-az. When not set,
         * floci-az detects it automatically.
         *
         * @param dockerHostOverride the hostname function containers use to reach floci-az ({@code null} to unset)
         * @return this builder
         */
        public Builder dockerHostOverride(String dockerHostOverride) {
            this.dockerHostOverride = dockerHostOverride;
            return this;
        }

        /**
         * Creates an immutable {@link FunctionsConfig} from this builder.
         *
         * @return the Functions configuration
         */
        @Override
        public FunctionsConfig build() {
            return new FunctionsConfig(this);
        }
    }
}
