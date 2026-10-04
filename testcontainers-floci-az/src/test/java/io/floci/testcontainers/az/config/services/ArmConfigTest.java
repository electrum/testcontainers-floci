package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ArmConfigTest {

    @Test
    void shouldApplyDefaultArmConfig() {
        ArmConfig config = ArmConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultSubscriptionId()).isEqualTo("00000000-0000-0000-0000-000000000001");
    }

    @Test
    void shouldApplyCustomArmConfig() {
        ArmConfig config = ArmConfig.builder()
                .enabled(false)
                .defaultSubscriptionId("22222222-2222-2222-2222-222222222222")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultSubscriptionId()).isEqualTo("22222222-2222-2222-2222-222222222222");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ArmConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ARM_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ARM_DEFAULT_SUBSCRIPTION_ID", "00000000-0000-0000-0000-000000000001");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ArmConfig.builder()
                .defaultSubscriptionId("22222222-2222-2222-2222-222222222222")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ARM_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_ARM_DEFAULT_SUBSCRIPTION_ID", "22222222-2222-2222-2222-222222222222");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ArmConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_ARM_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_ARM_DEFAULT_SUBSCRIPTION_ID");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ArmConfig config = ArmConfig.builder()
                .enabled(false)
                .defaultSubscriptionId("22222222-2222-2222-2222-222222222222")
                .build();
        ArmConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultSubscriptionId()).isEqualTo("22222222-2222-2222-2222-222222222222");
    }
}
