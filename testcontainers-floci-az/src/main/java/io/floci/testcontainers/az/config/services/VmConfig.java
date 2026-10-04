package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Virtual Machines of Floci Azure.
 *
 * <p>By default the service is {@code mocked}: virtual machines are pure ARM state and power actions
 * are state transitions only.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * VmConfig config = VmConfig.builder()
 *     .mocked(false)
 *     .defaultImage("ubuntu:24.04")
 *     .build();
 * }</pre>
 */
public class VmConfig extends AbstractServiceConfig<VmConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = true;
    private static final String DEFAULT_DEFAULT_IMAGE = "ubuntu:22.04";

    private final boolean mocked;
    private final String defaultImage;

    private VmConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.defaultImage = builder.defaultImage;
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
     * Returns whether no Docker container is started; virtual machines transition immediately to
     * {@code Succeeded} / {@code PowerState/running}.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the Docker image used when an image reference cannot be resolved (non-mocked mode).
     *
     * @return the Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_VM_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_VM_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_VM_DEFAULT_IMAGE", defaultImage);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link VmConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, VmConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;

        private Builder() {
            // Allow instantiation only via VmConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link VmConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(VmConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.defaultImage = instance.defaultImage;
        }

        /**
         * Sets whether no Docker container is started; virtual machines transition immediately to
         * {@code Succeeded} / {@code PowerState/running} and power actions are pure state transitions.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the Docker image used when an image reference cannot be resolved (non-mocked mode).
         *
         * @param defaultImage the Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Creates an immutable {@link VmConfig} from this builder.
         *
         * @return the Virtual Machines configuration
         */
        @Override
        public VmConfig build() {
            return new VmConfig(this);
        }
    }
}
