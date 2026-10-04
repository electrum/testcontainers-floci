package io.floci.testcontainers.az.services;

import com.azure.core.credential.AccessToken;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KeyVaultServiceTest extends AbstractServiceTest {

    @Test
    void shouldSetAndGetSecret() {
        // The Key Vault SDK only accepts https:// vault URLs
        SecretClient client = new SecretClientBuilder()
                .vaultUrl(floci.getHttpsEndpoint() + "/" + floci.getAccountName() + "-keyvault")
                .credential(request -> Mono.just(new AccessToken("test-token", OffsetDateTime.now().plusHours(1))))
                .disableChallengeResourceVerification()
                .httpClient(httpsClient())
                .buildClient();
        String name = "secret-" + UUID.randomUUID();

        client.setSecret(name, "s3cr3t");

        assertThat(client.getSecret(name).getValue()).isEqualTo("s3cr3t");
    }
}
