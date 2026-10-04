package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Azure Managed Identity of Floci Azure.
 *
 * <p>User-assigned identities, federated identity credentials and the IMDS token endpoint
 * ({@code /metadata/identity/oauth2/token}), issuing tokens signed with the Entra ID key.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ManagedIdentityConfig config = ManagedIdentityConfig.builder()
 *     .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
 *     .build();
 * }</pre>
 */
public class ManagedIdentityConfig extends AbstractServiceConfig<ManagedIdentityConfig.Builder> {

    private final String systemAssignedScope;

    private ManagedIdentityConfig(Builder builder) {
        super(builder);
        this.systemAssignedScope = builder.systemAssignedScope;
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
     * Returns the ARM scope that seeds the system-assigned IMDS identity. When empty, floci-az
     * uses {@code subscriptions/{default subscription id}}.
     *
     * @return the the system-assigned identity scope, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getSystemAssignedScope() {
        return Optional.ofNullable(systemAssignedScope);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {

            if (systemAssignedScope != null) {
                container.withEnv("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE", systemAssignedScope);
            }
        }
    }

    /**
     * Builder for {@link ManagedIdentityConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ManagedIdentityConfig> {

        private String systemAssignedScope;

        private Builder() {
            // Allow instantiation only via ManagedIdentityConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ManagedIdentityConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ManagedIdentityConfig instance) {
            super(instance);
            this.systemAssignedScope = instance.systemAssignedScope;
        }

        /**
         * Sets the ARM scope that seeds the system-assigned IMDS identity, e.g. the scope of the
         * resource the tested code pretends to run on. When not set, floci-az uses
         * {@code subscriptions/{default subscription id}}.
         *
         * @param systemAssignedScope the system-assigned identity scope ({@code null} to unset)
         * @return this builder
         */
        public Builder systemAssignedScope(String systemAssignedScope) {
            this.systemAssignedScope = systemAssignedScope;
            return this;
        }

        /**
         * Creates an immutable {@link ManagedIdentityConfig} from this builder.
         *
         * @return the Managed Identity configuration
         */
        @Override
        public ManagedIdentityConfig build() {
            return new ManagedIdentityConfig(this);
        }
    }
}
