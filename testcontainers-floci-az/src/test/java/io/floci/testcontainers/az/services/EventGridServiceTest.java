package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventGridServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2022-06-15";

    @Test
    void shouldCreateTopic() {
        String topic = createResourceGroup() + "/providers/Microsoft.EventGrid/topics/orders";

        RestResponse created = rest("PUT", topic + API_VERSION, "{\"location\":\"eastus\"}");
        RestResponse fetched = rest("GET", topic + API_VERSION, null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(fetched.json().path("name").asText()).isEqualTo("orders");
        assertThat(fetched.json().path("properties").path("endpoint").asText()).isNotBlank();
    }
}
