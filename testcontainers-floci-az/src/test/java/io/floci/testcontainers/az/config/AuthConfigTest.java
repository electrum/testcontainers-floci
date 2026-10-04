package io.floci.testcontainers.az.config;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.Map;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class AuthConfigTest {

    private static final String KEY = "bXktYmFzZTY0LWtleQ==";

    @Test
    void shouldApplyDefaultAuthConfig() {
        AuthConfig config = AuthConfig.builder().build();
        assertThat(config.getMode()).isEqualTo("dev");
        assertThat(config.getStorageAccountKeys()).isEmpty();
    }

    @Test
    void shouldApplyCustomAuthConfig() {
        AuthConfig config = AuthConfig.builder()
                .mode("strict")
                .storageAccountKey("myaccount", KEY)
                .storageAccountKey("otheraccount", "b3RoZXIta2V5")
                .build();
        assertThat(config.getMode()).isEqualTo("strict");
        assertThat(config.getStorageAccountKeys())
                .containsExactly(Map.entry("myaccount", KEY), Map.entry("otheraccount", "b3RoZXIta2V5"));
    }

    @Test
    void shouldReplaceStorageAccountKeys() {
        AuthConfig config = AuthConfig.builder()
                .storageAccountKey("myaccount", KEY)
                .storageAccountKeys(Map.of("otheraccount", "b3RoZXIta2V5"))
                .build();
        assertThat(config.getStorageAccountKeys()).containsExactly(Map.entry("otheraccount", "b3RoZXIta2V5"));
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_AUTH_MODE", "dev")
                .noneSatisfy((key, value) -> assertThat(key).startsWith("FLOCI_AZ_AUTH_STORAGE_ACCOUNT_KEYS_"));
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        AuthConfig.builder()
                .mode("strict")
                .storageAccountKey("myaccount", KEY)
                .storageAccountKey("devstoreaccount1", "b3RoZXIta2V5")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_AUTH_MODE", "strict")
                .containsEntry("FLOCI_AZ_AUTH_STORAGE_ACCOUNT_KEYS_MYACCOUNT", KEY)
                .containsEntry("FLOCI_AZ_AUTH_STORAGE_ACCOUNT_KEYS_DEVSTOREACCOUNT1", "b3RoZXIta2V5");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        AuthConfig config = AuthConfig.builder()
                .mode("strict")
                .storageAccountKey("myaccount", KEY)
                .build();

        AuthConfig copy = config.toBuilder().build();

        assertThat(copy.getMode()).isEqualTo("strict");
        assertThat(copy.getStorageAccountKeys()).containsExactly(Map.entry("myaccount", KEY));
    }

    @Test
    void shouldNotShareStorageAccountKeysWithBuilder() {
        AuthConfig.Builder builder = AuthConfig.builder().storageAccountKey("myaccount", KEY);
        AuthConfig config = builder.build();

        builder.storageAccountKey("otheraccount", "b3RoZXIta2V5");

        assertThat(config.getStorageAccountKeys()).containsOnlyKeys("myaccount");
    }
}
