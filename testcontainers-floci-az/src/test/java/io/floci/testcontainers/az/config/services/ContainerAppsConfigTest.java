package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ContainerAppsConfigTest {

    @Test
    void shouldApplyDefaultContainerAppsConfig() {
        ContainerAppsConfig config = ContainerAppsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getDnsSuffix()).isEqualTo("azurecontainerapps.io");
        assertThat(config.getIngressTimeoutSeconds()).isEqualTo(60);
    }

    @Test
    void shouldApplyCustomContainerAppsConfig() {
        ContainerAppsConfig config = ContainerAppsConfig.builder()
                .enabled(false)
                .mocked(false)
                .dnsSuffix("apps.example.test")
                .ingressTimeoutSeconds(30)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getDnsSuffix()).isEqualTo("apps.example.test");
        assertThat(config.getIngressTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ContainerAppsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_DNS_SUFFIX", "azurecontainerapps.io")
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_INGRESS_TIMEOUT_SECONDS", "60");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ContainerAppsConfig.builder()
                .mocked(false)
                .dnsSuffix("apps.example.test")
                .ingressTimeoutSeconds(30)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_DNS_SUFFIX", "apps.example.test")
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_INGRESS_TIMEOUT_SECONDS", "30");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ContainerAppsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_CONTAINER_APPS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_CONTAINER_APPS_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_CONTAINER_APPS_DNS_SUFFIX")
                .doesNotContainKey("FLOCI_AZ_SERVICES_CONTAINER_APPS_INGRESS_TIMEOUT_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ContainerAppsConfig config = ContainerAppsConfig.builder()
                .enabled(false)
                .mocked(false)
                .dnsSuffix("apps.example.test")
                .ingressTimeoutSeconds(30)
                .build();
        ContainerAppsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isFalse();
        assertThat(copy.getDnsSuffix()).isEqualTo("apps.example.test");
        assertThat(copy.getIngressTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(ContainerAppsConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(ContainerAppsConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(ContainerAppsConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
