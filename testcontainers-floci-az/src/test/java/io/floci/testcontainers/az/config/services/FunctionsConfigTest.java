package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class FunctionsConfigTest {

    @Test
    void shouldApplyDefaultFunctionsConfig() {
        FunctionsConfig config = FunctionsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getCodePath()).isEmpty();
        assertThat(config.isEphemeral()).isFalse();
        assertThat(config.getContainerIdleTimeoutSeconds()).isEqualTo(300);
        assertThat(config.getDockerHostOverride()).isEmpty();
    }

    @Test
    void shouldApplyCustomFunctionsConfig() {
        FunctionsConfig config = FunctionsConfig.builder()
                .enabled(false)
                .mocked(true)
                .codePath("/app/data/functions")
                .ephemeral(true)
                .containerIdleTimeoutSeconds(60)
                .dockerHostOverride("host.docker.internal")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getCodePath()).contains("/app/data/functions");
        assertThat(config.isEphemeral()).isTrue();
        assertThat(config.getContainerIdleTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getDockerHostOverride()).contains("host.docker.internal");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FunctionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_MOCKED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_FUNCTIONS_CODE_PATH")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_EPHEMERAL", "false")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_CONTAINER_IDLE_TIMEOUT_SECONDS", "300")
                .doesNotContainKey("FLOCI_AZ_SERVICES_FUNCTIONS_DOCKER_HOST_OVERRIDE");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        FunctionsConfig.builder()
                .mocked(true)
                .codePath("/app/data/functions")
                .ephemeral(true)
                .containerIdleTimeoutSeconds(60)
                .dockerHostOverride("host.docker.internal")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_CODE_PATH", "/app/data/functions")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_EPHEMERAL", "true")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_CONTAINER_IDLE_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_DOCKER_HOST_OVERRIDE", "host.docker.internal");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        FunctionsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_FUNCTIONS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_FUNCTIONS_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_FUNCTIONS_CODE_PATH")
                .doesNotContainKey("FLOCI_AZ_SERVICES_FUNCTIONS_EPHEMERAL")
                .doesNotContainKey("FLOCI_AZ_SERVICES_FUNCTIONS_CONTAINER_IDLE_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_FUNCTIONS_DOCKER_HOST_OVERRIDE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        FunctionsConfig config = FunctionsConfig.builder()
                .enabled(false)
                .mocked(true)
                .codePath("/app/data/functions")
                .ephemeral(true)
                .containerIdleTimeoutSeconds(60)
                .dockerHostOverride("host.docker.internal")
                .build();
        FunctionsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getCodePath()).contains("/app/data/functions");
        assertThat(copy.isEphemeral()).isTrue();
        assertThat(copy.getContainerIdleTimeoutSeconds()).isEqualTo(60);
        assertThat(copy.getDockerHostOverride()).contains("host.docker.internal");
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(FunctionsConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(FunctionsConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(FunctionsConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
