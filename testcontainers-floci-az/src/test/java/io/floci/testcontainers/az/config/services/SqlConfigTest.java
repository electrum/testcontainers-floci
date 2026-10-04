package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SqlConfigTest {

    @Test
    void shouldApplyDefaultSqlConfig() {
        SqlConfig config = SqlConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDataPlaneProvider()).isEmpty();
        assertThat(config.getAcceptEula()).isEqualTo("N");
        assertThat(config.getImage()).isEqualTo("mcr.microsoft.com/mssql/server:2025-latest");
        assertThat(config.getSaPassword()).isEqualTo("FlociAz_Strong123!");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(60);
        assertThat(config.getDefaultPort()).isEqualTo(0);
    }

    @Test
    void shouldApplyCustomSqlConfig() {
        SqlConfig config = SqlConfig.builder()
                .enabled(false)
                .dataPlaneProvider(SqlConfig.SqlDataPlaneProvider.MANAGED)
                .acceptEula("Y")
                .image("mcr.microsoft.com/mssql/server:2022-latest")
                .saPassword("My_Str0ng_Passw0rd!")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDataPlaneProvider()).contains(SqlConfig.SqlDataPlaneProvider.MANAGED);
        assertThat(config.getAcceptEula()).isEqualTo("Y");
        assertThat(config.getImage()).isEqualTo("mcr.microsoft.com/mssql/server:2022-latest");
        assertThat(config.getSaPassword()).isEqualTo("My_Str0ng_Passw0rd!");
        assertThat(config.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(config.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SqlConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SQL_ENABLED", "true")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SQL_DATA_PLANE_PROVIDER")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_ACCEPT_EULA", "N")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_IMAGE", "mcr.microsoft.com/mssql/server:2025-latest")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_SA_PASSWORD", "FlociAz_Strong123!")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_STARTUP_TIMEOUT_SECONDS", "60")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_DEFAULT_PORT", "0");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SqlConfig.builder()
                .dataPlaneProvider(SqlConfig.SqlDataPlaneProvider.MANAGED)
                .acceptEula("Y")
                .image("mcr.microsoft.com/mssql/server:2022-latest")
                .saPassword("My_Str0ng_Passw0rd!")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SQL_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_DATA_PLANE_PROVIDER", "managed")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_ACCEPT_EULA", "Y")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_IMAGE", "mcr.microsoft.com/mssql/server:2022-latest")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_SA_PASSWORD", "My_Str0ng_Passw0rd!")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_STARTUP_TIMEOUT_SECONDS", "120")
                .containsEntry("FLOCI_AZ_SERVICES_SQL_DEFAULT_PORT", "15432");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SqlConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_SQL_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SQL_DATA_PLANE_PROVIDER")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SQL_ACCEPT_EULA")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SQL_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SQL_SA_PASSWORD")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SQL_STARTUP_TIMEOUT_SECONDS")
                .doesNotContainKey("FLOCI_AZ_SERVICES_SQL_DEFAULT_PORT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SqlConfig config = SqlConfig.builder()
                .enabled(false)
                .dataPlaneProvider(SqlConfig.SqlDataPlaneProvider.MANAGED)
                .acceptEula("Y")
                .image("mcr.microsoft.com/mssql/server:2022-latest")
                .saPassword("My_Str0ng_Passw0rd!")
                .startupTimeoutSeconds(120)
                .defaultPort(15432)
                .build();
        SqlConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDataPlaneProvider()).contains(SqlConfig.SqlDataPlaneProvider.MANAGED);
        assertThat(copy.getAcceptEula()).isEqualTo("Y");
        assertThat(copy.getImage()).isEqualTo("mcr.microsoft.com/mssql/server:2022-latest");
        assertThat(copy.getSaPassword()).isEqualTo("My_Str0ng_Passw0rd!");
        assertThat(copy.getStartupTimeoutSeconds()).isEqualTo(120);
        assertThat(copy.getDefaultPort()).isEqualTo(15432);
    }

    @Test
    void shouldRequireDockerSocketOnlyForManagedDataPlane() {
        assertThat(SqlConfig.builder().build().getEffectiveDataPlaneProvider()).isEqualTo(SqlConfig.SqlDataPlaneProvider.NONE);
        assertThat(SqlConfig.builder().build().requiresDockerSocket()).isFalse();

        assertThat(SqlConfig.builder().acceptEula("Y").build().getEffectiveDataPlaneProvider())
                .isEqualTo(SqlConfig.SqlDataPlaneProvider.MANAGED);
        assertThat(SqlConfig.builder().acceptEula("Y").build().requiresDockerSocket()).isTrue();

        SqlConfig external = SqlConfig.builder().acceptEula("Y").dataPlaneProvider(SqlConfig.SqlDataPlaneProvider.EXTERNAL).build();
        assertThat(external.getEffectiveDataPlaneProvider()).isEqualTo(SqlConfig.SqlDataPlaneProvider.EXTERNAL);
        assertThat(external.requiresDockerSocket()).isFalse();

        assertThat(SqlConfig.builder().dataPlaneProvider(SqlConfig.SqlDataPlaneProvider.MANAGED).build().requiresDockerSocket()).isTrue();
        assertThat(SqlConfig.builder().acceptEula("Y").enabled(false).build().requiresDockerSocket()).isFalse();
    }
}
