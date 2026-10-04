package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SignalRConfigTest {

    @Test
    void shouldApplyDefaultSignalRConfig() {
        SignalRConfig config = SignalRConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getAccessKey()).isEqualTo("bG9jYWwtc2lnbmFsci1kZXZlbG9wbWVudC1rZXk=");
    }

    @Test
    void shouldApplyCustomSignalRConfig() {
        SignalRConfig config = SignalRConfig.builder()
                .enabled(false)
                .accessKey("bXktc2lnbmFsci1rZXk=")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getAccessKey()).isEqualTo("bXktc2lnbmFsci1rZXk=");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SignalRConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SIGNALR_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_SIGNALR_ACCESS_KEY", "bG9jYWwtc2lnbmFsci1kZXZlbG9wbWVudC1rZXk=");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SignalRConfig.builder()
                .accessKey("bXktc2lnbmFsci1rZXk=")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SIGNALR_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_SIGNALR_ACCESS_KEY", "bXktc2lnbmFsci1rZXk=");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SignalRConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SIGNALR_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SIGNALR_ACCESS_KEY");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SignalRConfig config = SignalRConfig.builder()
                .enabled(false)
                .accessKey("bXktc2lnbmFsci1rZXk=")
                .build();
        SignalRConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getAccessKey()).isEqualTo("bXktc2lnbmFsci1rZXk=");
    }
}
