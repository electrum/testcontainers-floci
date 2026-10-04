package io.floci.testcontainers.az;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that every configuration exposed by {@link FlociAzContainer} is actually picked up by the
 * container: the changed value survives the {@code with*Config(...)} round-trip, the matching
 * {@code FLOCI_AZ_*} environment variable is applied, and the container's port mappings are wired.
 *
 * <p>There is exactly one test method per service config class in {@code config/services/} plus one
 * per cross-cutting config class in {@code config/}.
 */
class FlociAzContainerServicesConfigTest {

    /**
     * Applies {@code configurer} to a fresh {@link FlociAzContainer}, then asserts that:
     * <ol>
     *   <li>the changed property is readable again via {@code get*Config()}
     *       ({@code actualValue} equals {@code expectedValue}),</li>
     *   <li>the container has the environment variable {@code envKey=envValue}, and</li>
     *   <li>the container exposes the main Floci Azure port.</li>
     * </ol>
     */
    private static void assertConfigWired(
            Consumer<FlociAzContainer> configurer,
            Function<FlociAzContainer, Object> actualValue,
            Object expectedValue,
            String envKey,
            String envValue) {
        try (FlociAzContainer container = new FlociAzContainer()) {
            configurer.accept(container);

            assertThat(actualValue.apply(container))
                    .as("value retrieved via get*Config()")
                    .isEqualTo(expectedValue);
            assertThat(container.getEnvMap())
                    .as("environment variable applied to the Floci Azure container")
                    .containsEntry(envKey, envValue);
            assertThat(container.getExposedPorts())
                    .as("port mapping configured on the Floci Azure container")
                    .contains(FlociAzContainer.PORT);
        }
    }

    // --- Service configs (config/services/) -------------------------------------------------------

    @Test
    void shouldWireQueueConfigIntoContainer() {
        assertConfigWired(
                c -> c.withQueueConfig(cfg -> cfg.enabled(false)),
                c -> c.getQueueConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_QUEUE_ENABLED", "false");
    }

    @Test
    void shouldWireTableConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTableConfig(cfg -> cfg.enabled(false)),
                c -> c.getTableConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_TABLE_ENABLED", "false");
    }

    @Test
    void shouldWireAppConfigConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAppConfigConfig(cfg -> cfg.enabled(false)),
                c -> c.getAppConfigConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_APP_CONFIG_ENABLED", "false");
    }

    @Test
    void shouldWireKeyVaultConfigIntoContainer() {
        assertConfigWired(
                c -> c.withKeyVaultConfig(cfg -> cfg.enabled(false)),
                c -> c.getKeyVaultConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_KEY_VAULT_ENABLED", "false");
    }
    @Test
    void shouldWireApimConfigIntoContainer() {
        assertConfigWired(
                c -> c.withApimConfig(cfg -> cfg.enabled(false)),
                c -> c.getApimConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_APIM_ENABLED", "false");
    }
    @Test
    void shouldWireMonitorConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMonitorConfig(cfg -> cfg.enabled(false)),
                c -> c.getMonitorConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_MONITOR_ENABLED", "false");
    }
    @Test
    void shouldWireGraphConfigIntoContainer() {
        assertConfigWired(
                c -> c.withGraphConfig(cfg -> cfg.enabled(false)),
                c -> c.getGraphConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_GRAPH_ENABLED", "false");
    }
    @Test
    void shouldWireNetworkConfigIntoContainer() {
        assertConfigWired(
                c -> c.withNetworkConfig(cfg -> cfg.enabled(false)),
                c -> c.getNetworkConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_NETWORK_ENABLED", "false");
    }
    @Test
    void shouldWireEmailConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEmailConfig(cfg -> cfg.enabled(false)),
                c -> c.getEmailConfig().isEnabled(), false,
                "FLOCI_AZ_SERVICES_EMAIL_ENABLED", "false");
    }

    // --- Cross-cutting configs (config/) ----------------------------------------------------------

    @Test
    void shouldWireAuthConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAuthConfig(cfg -> cfg.mode("strict")),
                c -> c.getAuthConfig().getMode(), "strict",
                "FLOCI_AZ_AUTH_MODE", "strict");
    }

    @Test
    void shouldWireTlsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTlsConfig(cfg -> cfg.enabled(true)),
                c -> c.getTlsConfig().isEnabled(), true,
                "FLOCI_AZ_TLS_ENABLED", "true");
    }
}
