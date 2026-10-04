package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AciConfigTest {

    @Test
    void shouldApplyDefaultAciConfig() {
        AciConfig config = AciConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getBasePort()).isEqualTo(7500);
        assertThat(config.getPortsCount()).isEqualTo(10);
        assertThat(config.getMaxPort()).isEqualTo(7509);
    }

    @Test
    void shouldApplyCustomAciConfig() {
        AciConfig config = AciConfig.builder()
                .enabled(false)
                .mocked(false)
                .portRange(17500, 5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getBasePort()).isEqualTo(17500);
        assertThat(config.getPortsCount()).isEqualTo(5);
        assertThat(config.getMaxPort()).isEqualTo(17504);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AciConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ACI_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ACI_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ACI_BASE_PORT", "7500")
                .containsEntry("FLOCI_AZ_SERVICES_ACI_MAX_PORT", "7509");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AciConfig.builder()
                .mocked(false)
                .portRange(17500, 5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ACI_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ACI_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_ACI_BASE_PORT", "17500")
                .containsEntry("FLOCI_AZ_SERVICES_ACI_MAX_PORT", "17504");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AciConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ACI_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ACI_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ACI_BASE_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ACI_MAX_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AciConfig config = AciConfig.builder()
                .enabled(false)
                .mocked(false)
                .portRange(17500, 5)
                .build();
        AciConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isFalse();
        assertThat(copy.getBasePort()).isEqualTo(17500);
        assertThat(copy.getPortsCount()).isEqualTo(5);
        assertThat(copy.getMaxPort()).isEqualTo(17504);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(AciConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(AciConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(AciConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
