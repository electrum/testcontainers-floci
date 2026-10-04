package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Table Storage of Floci Azure.
 *
 * <p>Table Storage is served under {@code /{account}-table} (see
 * {@code FlociAzContainer#getTableEndpoint()}).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * TableConfig config = TableConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class TableConfig extends AbstractServiceConfig<TableConfig.Builder> {

    private TableConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_TABLE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link TableConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, TableConfig> {

        private Builder() {
            // Allow instantiation only via TableConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link TableConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(TableConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link TableConfig} from this builder.
         *
         * @return the Table Storage configuration
         */
        @Override
        public TableConfig build() {
            return new TableConfig(this);
        }
    }
}
