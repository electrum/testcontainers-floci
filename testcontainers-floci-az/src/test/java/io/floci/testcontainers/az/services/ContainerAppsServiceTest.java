package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContainerAppsServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2024-03-01";

    @Test
    void shouldCreateManagedEnvironment() {
        // Container Apps are mocked by default: ARM state and revisions only
        String environment = createResourceGroup() + "/providers/Microsoft.App/managedEnvironments/env";

        RestResponse created = rest("PUT", environment + API_VERSION, "{\"location\":\"eastus\",\"properties\":{}}");
        RestResponse fetched = rest("GET", environment + API_VERSION, null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(fetched.json().path("properties").path("defaultDomain").asText()).endsWith("azurecontainerapps.io");
    }
}
