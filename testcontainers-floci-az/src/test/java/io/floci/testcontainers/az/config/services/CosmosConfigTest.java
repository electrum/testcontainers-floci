package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CosmosConfigTest {

    private static final List<String> APIS = List.of("NOSQL", "MONGODB", "POSTGRESQL", "CASSANDRA", "GREMLIN", "TABLE");

    @Test
    void shouldApplyDefaultCosmosConfig() {
        CosmosConfig config = CosmosConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMocked()).isFalse();
        assertThat(config.getEngineStartup()).isEqualTo("on-demand");
        assertThat(config.getDefaultApi()).isEqualTo("nosql");
        assertThat(List.of(config.getNosql(), config.getMongodb(), config.getPostgresql(), config.getCassandra(),
                config.getGremlin(), config.getTable())).allSatisfy(api -> {
            assertThat(api.isEnabled()).isFalse();
            assertThat(api.getImage()).isEmpty();
            assertThat(api.getPort()).isEmpty();
        });
    }

    @Test
    void shouldApplyCustomCosmosConfig() {
        CosmosConfig config = customConfig().enabled(false).build();
        assertThat(config.isEnabled()).isFalse();
        assertCustomValues(config);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CosmosConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_MOCKED", "false")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_STARTUP", "on-demand")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_DEFAULT_API", "nosql");
        APIS.forEach(api -> assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_" + api + "_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_ENGINES_" + api + "_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_ENGINES_" + api + "_PORT"));
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customConfig().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_MOCKED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_STARTUP", "eager")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_DEFAULT_API", "mongodb")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_NOSQL_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_NOSQL_PORT", "18081")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_MONGODB_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_MONGODB_IMAGE", "mongo:8")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_MONGODB_PORT", "27018")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_POSTGRESQL_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_POSTGRESQL_IMAGE", "citusdata/citus:13")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_CASSANDRA_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_CASSANDRA_IMAGE", "scylladb/scylla:6.3")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_GREMLIN_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_GREMLIN_PORT", "18182")
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENGINES_TABLE_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CosmosConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_COSMOS_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_MOCKED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_ENGINES_STARTUP")
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_ENGINES_DEFAULT_API");
        APIS.forEach(api -> assertThat(container.getEnvMap())
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_ENGINES_" + api + "_ENABLED")
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_ENGINES_" + api + "_IMAGE")
                .doesNotContainKey("FLOCI_AZ_SERVICES_COSMOS_ENGINES_" + api + "_PORT"));
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CosmosConfig copy = customConfig().enabled(false).build().toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertCustomValues(copy);
    }

    @Test
    void shouldKeepOtherApiValuesWhenReconfiguringAnApi() {
        CosmosConfig config = CosmosConfig.builder()
                .mongodb(api -> api.enabled(true).image("mongo:8"))
                .mongodb(api -> api.port(27018))
                .build();

        assertThat(config.getMongodb().isEnabled()).isTrue();
        assertThat(config.getMongodb().getImage()).contains("mongo:8");
        assertThat(config.getMongodb().getPort()).contains(27018);
    }

    @Test
    void shouldRequireDockerSocketOnlyForEnabledDockerBackedEngines() {
        assertThat(CosmosConfig.builder().build().requiresDockerSocket()).isFalse();
        assertThat(CosmosConfig.builder().nosql(api -> api.enabled(true)).table(api -> api.enabled(true)).build()
                .requiresDockerSocket()).isFalse();
        assertThat(CosmosConfig.builder().mongodb(api -> api.enabled(true)).build().requiresDockerSocket()).isTrue();
        assertThat(CosmosConfig.builder().gremlin(api -> api.enabled(true)).mocked(true).build().requiresDockerSocket()).isFalse();
        assertThat(CosmosConfig.builder().cassandra(api -> api.enabled(true)).engineStartup("disabled").build()
                .requiresDockerSocket()).isFalse();
        assertThat(CosmosConfig.builder().postgresql(api -> api.enabled(true)).enabled(false).build()
                .requiresDockerSocket()).isFalse();
    }

    private static CosmosConfig.Builder customConfig() {
        return CosmosConfig.builder()
                .mocked(true)
                .engineStartup("eager")
                .defaultApi("mongodb")
                .nosql(api -> api.enabled(true).port(18081))
                .mongodb(api -> api.enabled(true).image("mongo:8").port(27018))
                .postgresql(api -> api.enabled(true).image("citusdata/citus:13"))
                .cassandra(api -> api.enabled(true).image("scylladb/scylla:6.3"))
                .gremlin(api -> api.enabled(true).port(18182))
                .table(api -> api.enabled(true));
    }

    private static void assertCustomValues(CosmosConfig config) {
        assertThat(config.isMocked()).isTrue();
        assertThat(config.getEngineStartup()).isEqualTo("eager");
        assertThat(config.getDefaultApi()).isEqualTo("mongodb");
        assertThat(config.getNosql().isEnabled()).isTrue();
        assertThat(config.getNosql().getPort()).contains(18081);
        assertThat(config.getMongodb().isEnabled()).isTrue();
        assertThat(config.getMongodb().getImage()).contains("mongo:8");
        assertThat(config.getMongodb().getPort()).contains(27018);
        assertThat(config.getPostgresql().isEnabled()).isTrue();
        assertThat(config.getPostgresql().getImage()).contains("citusdata/citus:13");
        assertThat(config.getCassandra().isEnabled()).isTrue();
        assertThat(config.getCassandra().getImage()).contains("scylladb/scylla:6.3");
        assertThat(config.getGremlin().isEnabled()).isTrue();
        assertThat(config.getGremlin().getPort()).contains(18182);
        assertThat(config.getTable().isEnabled()).isTrue();
    }
}
