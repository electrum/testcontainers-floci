package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.List;

/**
 * Configuration for Azure Blob Storage of Floci Azure.
 *
 * <p>Blob Storage is served under {@code /{account}} (see {@code FlociAzContainer#getBlobEndpoint()}),
 * including the ADLS Gen2 DFS filesystem and path operations.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BlobConfig config = BlobConfig.builder()
 *     .hierarchicalNamespaceAccounts(List.of("devstoreaccount1", "datalake1"))
 *     .build();
 * }</pre>
 */
public class BlobConfig extends AbstractServiceConfig<BlobConfig.Builder> {

    private static final List<String> DEFAULT_HIERARCHICAL_NAMESPACE_ACCOUNTS = List.of("devstoreaccount1");

    private final List<String> hierarchicalNamespaceAccounts;

    private BlobConfig(Builder builder) {
        super(builder);
        this.hierarchicalNamespaceAccounts = List.copyOf(builder.hierarchicalNamespaceAccounts);
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
     * Returns the storage accounts that have the hierarchical namespace (ADLS Gen2) enabled.
     *
     * @return the storage accounts with hierarchical namespace
     */
    public List<String> getHierarchicalNamespaceAccounts() {
        return hierarchicalNamespaceAccounts;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_BLOB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_BLOB_HIERARCHICAL_NAMESPACE_ACCOUNTS", String.join(",", hierarchicalNamespaceAccounts));
        }
    }

    /**
     * Builder for {@link BlobConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BlobConfig> {

        private List<String> hierarchicalNamespaceAccounts = DEFAULT_HIERARCHICAL_NAMESPACE_ACCOUNTS;

        private Builder() {
            // Allow instantiation only via BlobConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BlobConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BlobConfig instance) {
            super(instance);
            this.hierarchicalNamespaceAccounts = instance.hierarchicalNamespaceAccounts;
        }

        /**
         * Sets the storage accounts that have the hierarchical namespace (ADLS Gen2) enabled.
         *
         * @param hierarchicalNamespaceAccounts the storage account names (default {@code devstoreaccount1})
         * @return this builder
         */
        public Builder hierarchicalNamespaceAccounts(List<String> hierarchicalNamespaceAccounts) {
            this.hierarchicalNamespaceAccounts = hierarchicalNamespaceAccounts;
            return this;
        }

        /**
         * Creates an immutable {@link BlobConfig} from this builder.
         *
         * @return the Blob Storage configuration
         */
        @Override
        public BlobConfig build() {
            return new BlobConfig(this);
        }
    }
}
