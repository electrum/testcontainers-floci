package io.floci.testcontainers.core;

import org.testcontainers.utility.DockerImageName;

import java.util.function.Consumer;

/**
 * Minimal {@link AbstractFlociContainer} subclass with two dummy services, used to test the provider-independent
 * container logic without depending on any real Floci emulator.
 */
class TestFlociContainer extends AbstractFlociContainer<TestFlociContainer> {

    static final DockerImageName IMAGE_NAME = DockerImageName.parse("floci/floci-test");
    static final int PORT = 4500;
    static final String LOG_LEVEL_ENV_VAR = "QUARKUS_LOG_CATEGORY__IO_FLOCI_TEST__LEVEL";
    static final String DOCKER_NETWORK_ENV_VAR = "FLOCI_TEST_SERVICES_DOCKER_NETWORK";

    private final ServiceConfigRef<SimpleServiceConfig> simpleConfig = registerServiceConfig(SimpleServiceConfig.builder().build());
    private final ServiceConfigRef<DockerBackedServiceConfig> dockerBackedConfig = registerServiceConfig(DockerBackedServiceConfig.builder().build());

    private String globalSetting = "default";

    TestFlociContainer() {
        this(IMAGE_NAME.withTag("latest"));
    }

    TestFlociContainer(DockerImageName dockerImageName) {
        super(dockerImageName, IMAGE_NAME, PORT, LOG_LEVEL_ENV_VAR, DOCKER_NETWORK_ENV_VAR);
        applyAllConfigs();
    }

    @Override
    protected void applyGlobalEnvVars() {
        withEnv("FLOCI_TEST_GLOBAL_SETTING", globalSetting);
    }

    TestFlociContainer withGlobalSetting(String globalSetting) {
        this.globalSetting = globalSetting;
        applyGlobalEnvVars();
        return this;
    }

    SimpleServiceConfig getSimpleConfig() {
        return simpleConfig.get();
    }

    TestFlociContainer withSimpleConfig(Consumer<SimpleServiceConfig.Builder> configurer) {
        return updateServiceConfig(simpleConfig, configurer);
    }

    DockerBackedServiceConfig getDockerBackedConfig() {
        return dockerBackedConfig.get();
    }

    TestFlociContainer withDockerBackedConfig(Consumer<DockerBackedServiceConfig.Builder> configurer) {
        return updateServiceConfig(dockerBackedConfig, configurer);
    }
}
