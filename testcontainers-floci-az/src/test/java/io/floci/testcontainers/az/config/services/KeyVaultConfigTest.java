package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class KeyVaultConfigTest {

    @Test
    void shouldApplyDefaultKeyVaultConfig() {
        KeyVaultConfig config = KeyVaultConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomKeyVaultConfig() {
        KeyVaultConfig config = KeyVaultConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KeyVaultConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_KEY_VAULT_ENABLED", "true");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        KeyVaultConfig.builder()
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_KEY_VAULT_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        KeyVaultConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_KEY_VAULT_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        KeyVaultConfig config = KeyVaultConfig.builder()
                .enabled(false)
                .build();
        KeyVaultConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}
