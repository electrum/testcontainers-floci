package io.floci.testcontainers.core;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;
import org.testcontainers.images.builder.Transferable;

/**
 * Dummy Docker-backed service with a mock mode, an extra exposed port and a file mounted into the container.
 */
class DockerBackedServiceConfig extends AbstractServiceConfig<DockerBackedServiceConfig.Builder> {

    static final int SERVICE_PORT = 4510;
    static final String FILE_PATH = "/tmp/floci-test-service.json";

    private final boolean mock;
    private final String fileContent;

    private DockerBackedServiceConfig(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.fileContent = builder.fileContent;
    }

    static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    boolean isMock() {
        return mock;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_TEST_SERVICES_DOCKER_BACKED_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_TEST_SERVICES_DOCKER_BACKED_MOCK", String.valueOf(mock));
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            container.addExposedPorts(SERVICE_PORT);
        }
    }

    @Override
    public void applyFileMountsToContainer(Container<?> container) {
        if (isEnabled() && fileContent != null) {
            container.withCopyToContainer(Transferable.of(fileContent), FILE_PATH);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    static class Builder extends AbstractServiceConfigBuilder<Builder, DockerBackedServiceConfig> {

        private boolean mock;
        private String fileContent;

        private Builder() {
        }

        private Builder(DockerBackedServiceConfig instance) {
            super(instance);
            this.mock = instance.mock;
            this.fileContent = instance.fileContent;
        }

        Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        Builder fileContent(String fileContent) {
            this.fileContent = fileContent;
            return this;
        }

        @Override
        public DockerBackedServiceConfig build() {
            return new DockerBackedServiceConfig(this);
        }
    }
}
