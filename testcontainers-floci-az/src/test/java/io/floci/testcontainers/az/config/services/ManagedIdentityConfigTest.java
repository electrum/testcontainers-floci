package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ManagedIdentityConfigTest {

    @Test
    void shouldApplyDefaultManagedIdentityConfig() {
        ManagedIdentityConfig config = ManagedIdentityConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getSystemAssignedScope()).isEmpty();
    }

    @Test
    void shouldApplyCustomManagedIdentityConfig() {
        ManagedIdentityConfig config = ManagedIdentityConfig.builder()
                .enabled(false)
                .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getSystemAssignedScope()).contains("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagedIdentityConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", "true")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagedIdentityConfig.builder()
                .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE", "subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ManagedIdentityConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ManagedIdentityConfig config = ManagedIdentityConfig.builder()
                .enabled(false)
                .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
                .build();
        ManagedIdentityConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getSystemAssignedScope()).contains("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app");
    }
}
