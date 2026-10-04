package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonitorServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateLogAnalyticsWorkspace() {
        String workspace = createResourceGroup() + "/providers/Microsoft.OperationalInsights/workspaces/logs";

        RestResponse created = rest("PUT", workspace + "?api-version=2022-10-01", "{\"location\":\"eastus\"}");
        RestResponse fetched = rest("GET", workspace + "?api-version=2022-10-01", null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(fetched.json().path("name").asText()).isEqualTo("logs");
        assertThat(fetched.json().path("type").asText()).isEqualTo("Microsoft.OperationalInsights/workspaces");
    }
}
