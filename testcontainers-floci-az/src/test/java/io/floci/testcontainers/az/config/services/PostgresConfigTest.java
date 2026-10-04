package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class PostgresConfigTest {

    @Test
    void shouldApplyDefaultPostgresConfig() {
        PostgresConfig config = PostgresConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getImage()).isEqualTo("postgres:17-alpine");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getDefaultPort()).isEqualTo(0);
    }

    @Test
    void shouldApplyCustomPostgresConfig() {
        PostgresConfig config = PostgresConfig.builder()
                .enabled(false)
                .mocked(true)
                .image("postgres:16-alpine")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getImage()).isEqualTo("postgres:16-alpine");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(config.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PostgresConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_IMAGE", "postgres:17-alpine")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_STARTUP_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_DEFAULT_PORT", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PostgresConfig.builder()
                .mocked(true)
                .image("postgres:16-alpine")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_IMAGE", "postgres:16-alpine")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_STARTUP_TIMEOUT_SECONDS", "120")
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_DEFAULT_PORT", "15432");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PostgresConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_POSTGRES_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_POSTGRES_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_POSTGRES_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_POSTGRES_STARTUP_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_POSTGRES_DEFAULT_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PostgresConfig config = PostgresConfig.builder()
                .enabled(false)
                .mocked(true)
                .image("postgres:16-alpine")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        PostgresConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getImage()).isEqualTo("postgres:16-alpine");
        assertThat(copy.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(copy.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(PostgresConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(PostgresConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(PostgresConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
