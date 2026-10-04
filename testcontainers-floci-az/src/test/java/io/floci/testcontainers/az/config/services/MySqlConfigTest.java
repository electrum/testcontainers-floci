package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MySqlConfigTest {

    @Test
    void shouldApplyDefaultMySqlConfig() {
        MySqlConfig config = MySqlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getImage()).isEqualTo("mysql:8.0");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getDefaultPort()).isEqualTo(0);
    }

    @Test
    void shouldApplyCustomMySqlConfig() {
        MySqlConfig config = MySqlConfig.builder()
                .enabled(false)
                .mocked(true)
                .image("mysql:8.4")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getImage()).isEqualTo("mysql:8.4");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(config.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MySqlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_IMAGE", "mysql:8.0")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_STARTUP_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_DEFAULT_PORT", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MySqlConfig.builder()
                .mocked(true)
                .image("mysql:8.4")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_IMAGE", "mysql:8.4")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_STARTUP_TIMEOUT_SECONDS", "120")
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_DEFAULT_PORT", "15432");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        MySqlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MYSQL_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MYSQL_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MYSQL_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MYSQL_STARTUP_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MYSQL_DEFAULT_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MySqlConfig config = MySqlConfig.builder()
                .enabled(false)
                .mocked(true)
                .image("mysql:8.4")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        MySqlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getImage()).isEqualTo("mysql:8.4");
        assertThat(copy.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(copy.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(MySqlConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(MySqlConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(MySqlConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
