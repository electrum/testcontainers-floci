package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Key Vault of Floci Azure.
 *
 * <p>Key Vault (secrets, keys and certificates) is served under {@code /{account}-keyvault}.
 * The Azure Key Vault SDKs require HTTPS, see {@code FlociAzContainer#withTlsConfig}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * KeyVaultConfig config = KeyVaultConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class KeyVaultConfig extends AbstractServiceConfig<KeyVaultConfig.Builder> {

    private KeyVaultConfig(Builder builder) {
        super(builder);
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_KEY_VAULT_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link KeyVaultConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KeyVaultConfig> {

        private Builder() {
            // Allow instantiation only via KeyVaultConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link KeyVaultConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(KeyVaultConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link KeyVaultConfig} from this builder.
         *
         * @return the Key Vault configuration
         */
        @Override
        public KeyVaultConfig build() {
            return new KeyVaultConfig(this);
        }
    }
}
