package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AcrConfigTest {

    @Test
    void shouldApplyDefaultAcrConfig() {
        AcrConfig config = AcrConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("registry:2");
        assertThat(config.getBasePort()).isEqualTo(5000);
        assertThat(config.getPortsCount()).isEqualTo(10);
        assertThat(config.getMaxPort()).isEqualTo(5009);
    }

    @Test
    void shouldApplyCustomAcrConfig() {
        AcrConfig config = AcrConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultImage("registry:3")
                .portRange(15000, 5)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("registry:3");
        assertThat(config.getBasePort()).isEqualTo(15000);
        assertThat(config.getPortsCount()).isEqualTo(5);
        assertThat(config.getMaxPort()).isEqualTo(15004);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AcrConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ACR_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_DEFAULT_IMAGE", "registry:2")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_BASE_PORT", "5000")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_MAX_PORT", "5009");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AcrConfig.builder()
                .mocked(true)
                .defaultImage("registry:3")
                .portRange(15000, 5)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ACR_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_DEFAULT_IMAGE", "registry:3")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_BASE_PORT", "15000")
                .containsEntry("FLOCI_AZ_SERVICES_ACR_MAX_PORT", "15004");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AcrConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ACR_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ACR_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ACR_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ACR_BASE_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ACR_MAX_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AcrConfig config = AcrConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultImage("registry:3")
                .portRange(15000, 5)
                .build();
        AcrConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("registry:3");
        assertThat(copy.getBasePort()).isEqualTo(15000);
        assertThat(copy.getPortsCount()).isEqualTo(5);
        assertThat(copy.getMaxPort()).isEqualTo(15004);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(AcrConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(AcrConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(AcrConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
