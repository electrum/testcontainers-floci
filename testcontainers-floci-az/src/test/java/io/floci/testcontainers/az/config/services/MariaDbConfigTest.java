package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class MariaDbConfigTest {

    @Test
    void shouldApplyDefaultMariaDbConfig() {
        MariaDbConfig config = MariaDbConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getImage()).isEqualTo("mariadb:10.11");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getDefaultPort()).isEqualTo(0);
    }

    @Test
    void shouldApplyCustomMariaDbConfig() {
        MariaDbConfig config = MariaDbConfig.builder()
                .enabled(false)
                .mocked(true)
                .image("mariadb:11.4")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getImage()).isEqualTo("mariadb:11.4");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(config.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MariaDbConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_IMAGE", "mariadb:10.11")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_STARTUP_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_DEFAULT_PORT", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        MariaDbConfig.builder()
                .mocked(true)
                .image("mariadb:11.4")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_IMAGE", "mariadb:11.4")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_STARTUP_TIMEOUT_SECONDS", "120")
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_DEFAULT_PORT", "15432");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        MariaDbConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MARIA_DB_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MARIA_DB_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MARIA_DB_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MARIA_DB_STARTUP_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MARIA_DB_DEFAULT_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        MariaDbConfig config = MariaDbConfig.builder()
                .enabled(false)
                .mocked(true)
                .image("mariadb:11.4")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        MariaDbConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMocked()).isTrue();
        assertThat(copy.getImage()).isEqualTo("mariadb:11.4");
        assertThat(copy.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(copy.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(MariaDbConfig.builder().mocked(false).build().requiresDockerSocket()).isTrue();
        assertThat(MariaDbConfig.builder().mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(MariaDbConfig.builder().enabled(false).mocked(false).build().requiresDockerSocket()).isFalse();
    }
}
