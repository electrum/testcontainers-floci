package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class GraphServiceTest extends AbstractServiceTest {

    @Test
    void shouldFindServicePrincipalByAppId() {
        String filter = URLEncoder.encode("appId eq 'my-client-id'", StandardCharsets.UTF_8);

        RestResponse response = rest("GET", "/v1.0/servicePrincipals?$filter=" + filter, null);

        assertThat(response.isSuccessful()).as(response.toString()).isTrue();
        assertThat(response.json().path("value").path(0).path("appId").asText()).isEqualTo("my-client-id");
    }
}
