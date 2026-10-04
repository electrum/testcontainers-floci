package io.floci.testcontainers.core.config.services;

import org.testcontainers.containers.Container;

/**
 * Base class for the service configurations of all Floci emulators.
 *
 * <p>Every service configuration supports an {@link #isEnabled()} flag and can apply its
 * settings to a container via {@link #applyEnvVarsToContainer(Container)}.
 *
 * @param <B> the concrete {@link AbstractServiceConfigBuilder} subtype used to build and rebuild
 *            this configuration, allowing {@link #toBuilder()} to be implemented in a type-safe
 *            way by subclasses
 */
public abstract class AbstractServiceConfig<B extends AbstractServiceConfigBuilder<B, ?>> {

    /** Default value for the {@link #isEnabled()} flag. */
    protected static final boolean DEFAULT_ENABLED = true;

    private final boolean enabled;

    /**
     * Creates a new service configuration with the given enabled flag.
     *
     * @param enabled {@code true} to enable the service
     */
    protected AbstractServiceConfig(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Creates a new service configuration with the enabled flag of the given builder. Prefer this constructor in
     * subclasses outside this package, since they cannot read the builder's protected
     * {@code enabled} field themselves.
     *
     * @param builder the builder to take the enabled flag from
     */
    protected AbstractServiceConfig(AbstractServiceConfigBuilder<?, ?> builder) {
        this(builder.enabled);
    }

    /**
     * Returns whether this service is enabled.
     *
     * @return {@code true} if this service is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns a new builder for this configuration, initialized with the current values of this
     * instance. Every subclass must implement this to return its own {@code Builder} type,
     * pre-populated via the builder's copy constructor.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    public abstract B toBuilder();

    /**
     * Applies this service configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
    }

    /**
     * Applies this service configuration to the given container by exposing
     * the appropriate ports.
     *
     * @param container the container to configure
     */
    public void applyExposedPortsToContainer(Container<?> container) {
    }

    /**
     * Applies this service configuration to the given container by mounting the files it needs
     * into the container (e.g. copying a generated configuration file to a path the Floci server
     * reads).
     *
     * <p>Called from the same places as {@link #applyEnvVarsToContainer(Container)} — from the
     * {@link io.floci.testcontainers.core.AbstractFlociContainer#applyAllConfigs()}, after every
     * {@code with<Service>Config(...)} call, and after {@code disableAllServices()} — so
     * implementations must tolerate being invoked more than once for the same configuration
     * (re-copying identical content to the same container path is harmless).
     *
     * @param container the container to configure
     */
    public void applyFileMountsToContainer(Container<?> container) {
    }

    /**
     * Returns whether this service, as currently configured, needs access to the host Docker socket to
     * create sibling containers (e.g. a database service spinning up a PostgreSQL container, or a
     * functions service invoking functions in child containers).
     *
     * <p>Defaults to {@code false}. Docker-backed services override this to return {@code true} while
     * {@linkplain #isEnabled() enabled} (and, for services that support a docker-less mock mode,
     * only while not running in that mode).
     *
     * @return {@code true} if this service requires the Docker socket to be mounted
     */
    public boolean requiresDockerSocket() {
        return false;
    }
}
