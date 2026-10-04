package io.floci.testcontainers.az.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class BlobConfigTest {

    @Test
    void shouldApplyDefaultBlobConfig() {
        BlobConfig config = BlobConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getHierarchicalNamespaceAccounts()).containsExactly("devstoreaccount1");
    }

    @Test
    void shouldApplyCustomBlobConfig() {
        BlobConfig config = BlobConfig.builder()
                .enabled(false)
                .hierarchicalNamespaceAccounts(List.of("devstoreaccount1", "datalake1"))
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getHierarchicalNamespaceAccounts()).containsExactly("devstoreaccount1", "datalake1");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BlobConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_BLOB_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_BLOB_HIERARCHICAL_NAMESPACE_ACCOUNTS", "devstoreaccount1");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        BlobConfig.builder()
                .hierarchicalNamespaceAccounts(List.of("devstoreaccount1", "datalake1"))
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_BLOB_ENABLED", "true")
                .containsEntry("FLOCI_AZ_SERVICES_BLOB_HIERARCHICAL_NAMESPACE_ACCOUNTS", "devstoreaccount1,datalake1");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        BlobConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_AZ_SERVICES_BLOB_ENABLED", "false")
                .doesNotContainKey("FLOCI_AZ_SERVICES_BLOB_HIERARCHICAL_NAMESPACE_ACCOUNTS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        BlobConfig config = BlobConfig.builder()
                .enabled(false)
                .hierarchicalNamespaceAccounts(List.of("devstoreaccount1", "datalake1"))
                .build();
        BlobConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getHierarchicalNamespaceAccounts()).containsExactly("devstoreaccount1", "datalake1");
    }
}
