package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApimServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2024-05-01";

    @Test
    void shouldCreateApiManagementService() {
        String service = createResourceGroup() + "/providers/Microsoft.ApiManagement/service/apim";

        RestResponse created = rest("PUT", service + API_VERSION, """
                {
                  "location": "eastus",
                  "sku": {"name": "Developer", "capacity": 1},
                  "properties": {"publisherEmail": "admin@example.com", "publisherName": "Floci"}
                }
                """);
        RestResponse fetched = rest("GET", service + API_VERSION, null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(fetched.json().path("name").asText()).isEqualTo("apim");
        assertThat(fetched.json().path("properties").path("publisherName").asText()).isEqualTo("Floci");
    }
}
