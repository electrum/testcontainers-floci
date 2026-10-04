package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AksConfigTest {

    @Test
    void shouldApplyDefaultAksConfig() {
        AksConfig config = AksConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("rancher/k3s:latest");
        assertThat(config.getApiServerBasePort()).isEqualTo(6443);
        assertThat(config.getApiServerPortsCount()).isEqualTo(10);
        assertThat(config.getApiServerMaxPort()).isEqualTo(6452);
        assertThat(config.isKeepRunningOnShutdown()).isFalse();
    }

    @Test
    void shouldApplyCustomAksConfig() {
        AksConfig config = AksConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultImage("rancher/k3s:v1.31.4-k3s1")
                .apiServerPortRange(16443, 5)
                .keepRunningOnShutdown(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("rancher/k3s:v1.31.4-k3s1");
        assertThat(config.getApiServerBasePort()).isEqualTo(16443);
        assertThat(config.getApiServerPortsCount()).isEqualTo(5);
        assertThat(config.getApiServerMaxPort()).isEqualTo(16447);
        assertThat(config.isKeepRunningOnShutdown()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AksConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_AKS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_DEFAULT_IMAGE", "rancher/k3s:latest")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_API_SERVER_BASE_PORT", "6443")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_API_SERVER_MAX_PORT", "6452")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_KEEP_RUNNING_ON_SHUTDOWN", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AksConfig.builder()
                .mocked(true)
                .defaultImage("rancher/k3s:v1.31.4-k3s1")
                .apiServerPortRange(16443, 5)
                .keepRunningOnShutdown(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_AKS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_DEFAULT_IMAGE", "rancher/k3s:v1.31.4-k3s1")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_API_SERVER_BASE_PORT", "16443")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_API_SERVER_MAX_PORT", "16447")
                .containsEntry("FLOCI_AZ_SERVICES_AKS_KEEP_RUNNING_ON_SHUTDOWN", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        AksConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_AKS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_AKS_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_AKS_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_AKS_API_SERVER_BASE_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_AKS_API_SERVER_MAX_PORT")
                .doesNotContainKey("FLOCI_AZ_SERVICES_AKS_KEEP_RUNNING_ON_SHUTDOWN");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AksConfig config = AksConfig.builder()
                .enabled(false)
                .mocked(true)
                .defaultImage("rancher/k3s:v1.31.4-k3s1")
                .apiServerPortRange(16443, 5)
                .keepRunningOnShutdown(true)
                .build();
        AksConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getDefaultImage()).isEqualTo("rancher/k3s:v1.31.4-k3s1");
        assertThat(copy.getApiServerBasePort()).isEqualTo(16443);
        assertThat(copy.getApiServerPortsCount()).isEqualTo(5);
        assertThat(copy.getApiServerMaxPort()).isEqualTo(16447);
        assertThat(copy.isKeepRunningOnShutdown()).isTrue();
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(AksConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(AksConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(AksConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
