package io.floci.testcontainers.core;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Dummy service with a single property that is applied as environment variable.
 */
class SimpleServiceConfig extends AbstractServiceConfig<SimpleServiceConfig.Builder> {

    private final String value;

    private SimpleServiceConfig(Builder builder) {
        super(builder);
        this.value = builder.value;
    }

    static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    String getValue() {
        return value;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_TEST_SERVICES_SIMPLE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_TEST_SERVICES_SIMPLE_VALUE", value);
        }
    }

    static class Builder extends AbstractServiceConfigBuilder<Builder, SimpleServiceConfig> {

        private String value = "default";

        private Builder() {
        }

        private Builder(SimpleServiceConfig instance) {
            super(instance);
            this.value = instance.value;
        }

        Builder value(String value) {
            this.value = value;
            return this;
        }

        @Override
        public SimpleServiceConfig build() {
            return new SimpleServiceConfig(this);
        }
    }
}
