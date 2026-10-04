package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NetworkServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2024-05-01";

    @Test
    void shouldCreateVirtualNetworkWithSubnet() {
        String vnet = createResourceGroup() + "/providers/Microsoft.Network/virtualNetworks/vnet";

        RestResponse created = rest("PUT", vnet + API_VERSION, """
                {
                  "location": "eastus",
                  "properties": {
                    "addressSpace": {"addressPrefixes": ["10.0.0.0/16"]},
                    "subnets": [{"name": "default", "properties": {"addressPrefix": "10.0.0.0/24"}}]
                  }
                }
                """);
        RestResponse fetched = rest("GET", vnet + API_VERSION, null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(created.json().path("properties").path("provisioningState").asText()).isEqualTo("Succeeded");
        assertThat(fetched.json().path("properties").path("subnets").path(0).path("properties").path("addressPrefix").asText())
                .isEqualTo("10.0.0.0/24");
    }
}
