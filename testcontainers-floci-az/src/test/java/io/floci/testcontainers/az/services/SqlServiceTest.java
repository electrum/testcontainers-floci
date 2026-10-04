package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SqlServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2021-11-01";

    @Test
    void shouldCreateServerAndDatabaseWithoutDataPlane() {
        String server = createResourceGroup() + "/providers/Microsoft.Sql/servers/sql-server";

        RestResponse createdServer = rest("PUT", server + API_VERSION, """
                {"location": "eastus", "properties": {"administratorLogin": "sa", "administratorLoginPassword": "FlociAz_Strong123!"}}
                """);
        RestResponse createdDatabase = rest("PUT", server + "/databases/db" + API_VERSION, "{\"location\":\"eastus\"}");
        RestResponse connect = rest("GET", "/" + floci.getAccountName() + "-sql/servers/sql-server/connect", null);

        assertThat(createdServer.isSuccessful()).as(createdServer.toString()).isTrue();
        assertThat(createdServer.json().path("properties").path("administratorLogin").asText()).isEqualTo("sa");
        assertThat(createdDatabase.isSuccessful()).as(createdDatabase.toString()).isTrue();
        // without an accepted EULA the default data plane provider is "none" (control plane only)
        assertThat(connect.status()).as(connect.toString()).isEqualTo(409);
    }
}
