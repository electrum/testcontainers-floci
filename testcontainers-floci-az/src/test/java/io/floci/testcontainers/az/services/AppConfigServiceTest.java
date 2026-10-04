package io.floci.testcontainers.az.services;

import com.azure.data.appconfiguration.ConfigurationClient;
import com.azure.data.appconfiguration.ConfigurationClientBuilder;
import com.azure.data.appconfiguration.models.ConfigurationSetting;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AppConfigServiceTest extends AbstractServiceTest {

    @Test
    void shouldSetAndGetConfigurationSetting() {
        // The App Configuration SDK only accepts https:// endpoints
        String endpoint = floci.getHttpsEndpoint() + "/" + floci.getAccountName() + "-appconfig";
        ConfigurationClient client = new ConfigurationClientBuilder()
                .connectionString("Endpoint=" + endpoint + ";Id=" + floci.getAccountName() + ";Secret=" + floci.getAccountKey())
                .httpClient(httpsClient())
                .buildClient();
        String key = "app:" + UUID.randomUUID();

        client.setConfigurationSetting(key, null, "hello");
        ConfigurationSetting setting = client.getConfigurationSetting(key, null);

        assertThat(setting.getValue()).isEqualTo("hello");
    }
}
