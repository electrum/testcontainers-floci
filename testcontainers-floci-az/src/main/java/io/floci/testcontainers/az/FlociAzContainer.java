package io.floci.testcontainers.az;

import io.floci.testcontainers.core.AbstractFlociContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Testcontainers module for <a href="https://github.com/floci-io/floci-az">Floci Azure</a> — a
 * free, open-source local Azure emulator.
 *
 * <p>Starts a Floci Azure container that exposes all emulated Azure services on a single HTTP
 * endpoint. Use {@link #getEndpoint()} to obtain the base URL, or one of the service-specific
 * helpers such as {@link #getBlobEndpoint()} and {@link #getStorageConnectionString()} to configure
 * Azure SDK clients.
 *
 * <p>Container-based services (Azure Functions, AKS, Azure Database for PostgreSQL and others) require
 * access to the Docker daemon. This module automatically mounts the Docker socket only when at least
 * one currently enabled service actually needs it. Use {@link #withDockerSocket(boolean)} to override
 * this auto-detection.
 *
 * <pre>{@code
 * try (FlociAzContainer floci = new FlociAzContainer()) {
 *     floci.start();
 *     BlobServiceClient client = new BlobServiceClientBuilder()
 *         .connectionString(floci.getStorageConnectionString())
 *         .buildClient();
 * }
 * }</pre>
 */
public class FlociAzContainer extends AbstractFlociContainer<FlociAzContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("floci/floci-az");
    private static final String DEFAULT_TAG = "latest";

    /**
     * Port Floci Azure serves all emulated services on.
     */
    public static final int PORT = 4577;

    private static final String LOG_LEVEL_ENV_VAR = "QUARKUS_LOG_CATEGORY__IO_FLOCI_AZ__LEVEL";
    private static final String DOCKER_NETWORK_ENV_VAR = "FLOCI_AZ_SERVICES_DOCKER_NETWORK";
    private static final String HEALTH_PATH = "/_floci/health";

    private static final String DEFAULT_ACCOUNT_NAME = "devstoreaccount1";
    private static final String DEFAULT_ACCOUNT_KEY =
            "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

    /**
     * Creates a new Floci Azure container with the default image ({@code floci/floci-az:latest}).
     */
    public FlociAzContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Creates a new Floci Azure container with the specified image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-az})
     */
    public FlociAzContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    /**
     * Creates a new Floci Azure container with the specified Docker image name.
     *
     * @param dockerImageName the Docker image name (must be compatible with {@code floci/floci-az})
     */
    public FlociAzContainer(DockerImageName dockerImageName) {
        super(dockerImageName, DEFAULT_IMAGE_NAME, PORT, LOG_LEVEL_ENV_VAR, DOCKER_NETWORK_ENV_VAR);

        waitingFor(Wait.forHttp(HEALTH_PATH)
                .forPort(PORT)
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofSeconds(60)));

        applyAllConfigs();
    }

    /**
     * Returns the name of the default storage account ({@value DEFAULT_ACCOUNT_NAME}). Floci Azure serves
     * the data planes of all storage services under paths prefixed with the account name.
     *
     * @return the storage account name
     */
    public String getAccountName() {
        return DEFAULT_ACCOUNT_NAME;
    }

    /**
     * Returns the key of the default storage account, i.e. the well-known development storage key.
     *
     * @return the base64-encoded storage account key
     */
    public String getAccountKey() {
        return DEFAULT_ACCOUNT_KEY;
    }

    /**
     * Returns the Blob Storage endpoint of the default storage account (e.g.
     * {@code http://localhost:32781/devstoreaccount1}).
     *
     * @return the Blob Storage endpoint
     */
    public String getBlobEndpoint() {
        return getEndpoint() + "/" + getAccountName();
    }

    /**
     * Returns the Queue Storage endpoint of the default storage account (e.g.
     * {@code http://localhost:32781/devstoreaccount1-queue}).
     *
     * @return the Queue Storage endpoint
     */
    public String getQueueEndpoint() {
        return getEndpoint() + "/" + getAccountName() + "-queue";
    }

    /**
     * Returns the Table Storage endpoint of the default storage account (e.g.
     * {@code http://localhost:32781/devstoreaccount1-table}).
     *
     * @return the Table Storage endpoint
     */
    public String getTableEndpoint() {
        return getEndpoint() + "/" + getAccountName() + "-table";
    }

    /**
     * Returns a storage connection string for the default storage account, with the Blob, Queue and
     * Table endpoints pointing at this container.
     *
     * @return the storage connection string
     */
    public String getStorageConnectionString() {
        return "DefaultEndpointsProtocol=http"
                + ";AccountName=" + getAccountName()
                + ";AccountKey=" + getAccountKey()
                + ";BlobEndpoint=" + getBlobEndpoint()
                + ";QueueEndpoint=" + getQueueEndpoint()
                + ";TableEndpoint=" + getTableEndpoint()
                + ";";
    }
}
