package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AciServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2023-05-01";

    @Test
    void shouldCreateMockedContainerGroup() {
        // Container Instances are mocked by default: no container is started
        String group = createResourceGroup() + "/providers/Microsoft.ContainerInstance/containerGroups/group";

        RestResponse created = rest("PUT", group + API_VERSION, """
                {
                  "location": "eastus",
                  "properties": {
                    "containers": [{"name": "web", "properties": {"image": "nginx:alpine", "ports": [{"port": 80}],
                                    "resources": {"requests": {"cpu": 1, "memoryInGB": 1}}}}],
                    "osType": "linux"
                  }
                }
                """);
        RestResponse fetched = rest("GET", group + API_VERSION, null);

        assertThat(created.status()).as(created.toString()).isEqualTo(201);
        assertThat(fetched.json().path("properties").path("provisioningState").asText()).isEqualTo("Succeeded");
    }
}
