package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class VmConfigTest {

    @Test
    void shouldApplyDefaultVmConfig() {
        VmConfig config = VmConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("ubuntu:22.04");
    }

    @Test
    void shouldApplyCustomVmConfig() {
        VmConfig config = VmConfig.builder()
                .enabled(false)
                .mocked(false)
                .defaultImage("ubuntu:24.04")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("ubuntu:24.04");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        VmConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_VM_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_VM_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_VM_DEFAULT_IMAGE", "ubuntu:22.04");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        VmConfig.builder()
                .mocked(false)
                .defaultImage("ubuntu:24.04")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_VM_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_VM_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_VM_DEFAULT_IMAGE", "ubuntu:24.04");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        VmConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_VM_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_VM_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_VM_DEFAULT_IMAGE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        VmConfig config = VmConfig.builder()
                .enabled(false)
                .mocked(false)
                .defaultImage("ubuntu:24.04")
                .build();
        VmConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isFalse();
        assertThat(copy.getDefaultImage()).isEqualTo("ubuntu:24.04");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(VmConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(VmConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(VmConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
