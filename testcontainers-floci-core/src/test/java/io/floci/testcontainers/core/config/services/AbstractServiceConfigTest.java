package io.floci.testcontainers.core.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractServiceConfigTest {

    @Test
    void shouldBeEnabledByDefault() {
        assertThat(MinimalConfig.builder().build().isEnabled()).isTrue();
    }

    @Test
    void shouldApplyEnabledFlag() {
        assertThat(MinimalConfig.builder().enabled(false).build().isEnabled()).isFalse();
    }

    @Test
    void shouldPreserveEnabledFlagOnToBuilder() {
        MinimalConfig copy = MinimalConfig.builder().enabled(false).build().toBuilder().build();

        assertThat(copy.isEnabled()).isFalse();
    }

    @Test
    void shouldNotTouchContainerByDefault() {
        GenericContainer<?> container = genericContainer();
        MinimalConfig config = MinimalConfig.builder().build();

        config.applyEnvVarsToContainer(container);
        config.applyExposedPortsToContainer(container);
        config.applyFileMountsToContainer(container);

        assertThat(container.getEnvMap()).isEmpty();
        assertThat(container.getExposedPorts()).isEmpty();
        assertThat(config.requiresDockerSocket()).isFalse();
    }

    private static final class MinimalConfig extends AbstractServiceConfig<MinimalConfig.Builder> {

        private MinimalConfig(Builder builder) {
            super(builder);
        }

        static Builder builder() {
            return new Builder();
        }

        @Override
        public Builder toBuilder() {
            return new Builder(this);
        }

        static final class Builder extends AbstractServiceConfigBuilder<Builder, MinimalConfig> {

            private Builder() {
            }

            private Builder(MinimalConfig instance) {
                super(instance);
            }

            @Override
            public MinimalConfig build() {
                return new MinimalConfig(this);
            }
        }
    }
}
