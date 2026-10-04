package io.floci.testcontainers.az.services;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipelineCallContext;
import com.azure.core.http.HttpPipelineNextPolicy;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.jdk.httpclient.JdkHttpClientBuilder;
import com.azure.core.http.policy.HttpPipelinePolicy;
import io.floci.testcontainers.az.FlociAzContainer;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Base class for Floci Azure service integration tests. Provides a shared {@link FlociAzContainer}
 * singleton (started once per JVM) with TLS enabled, since several Azure SDKs only talk HTTPS.
 */
abstract class AbstractServiceTest {

    // Integration tests run against the nightly build so newly added services are covered
    // before they land in a versioned release.
    private static final String NIGHTLY_IMAGE = "floci/floci-az:nightly";

    private static final boolean DEBUG_LOGGING = false;

    protected static final FlociAzContainer floci;

    private static SSLContext sslContext;

    static {
        if (DEBUG_LOGGING) {
            floci = new FlociAzContainer(NIGHTLY_IMAGE)
                    .withLogLevel(Level.DEBUG)
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DOCKER")));
        } else {
            floci = new FlociAzContainer(NIGHTLY_IMAGE)
                    .withLogLevel(Level.INFO);
        }

        floci.withTlsConfig(c -> c.enabled(true))
                .start();
    }

    /**
     * Returns an Azure SDK HTTP client that trusts the TLS certificate of the Floci Azure container, for SDKs that
     * require {@code https://} endpoints (see {@link FlociAzContainer#getHttpsEndpoint()}).
     */
    protected static HttpClient httpsClient() {
        return new JdkHttpClientBuilder(java.net.http.HttpClient.newBuilder().sslContext(sslContext())).build();
    }

    /**
     * Returns a pipeline policy that redirects requests to URLs Floci Azure generated from its own base URL
     * ({@code http://localhost:4577}, e.g. long-running-operation polling URLs) to the HTTPS endpoint of the
     * container, since the container port is mapped to a random host port.
     */
    protected static HttpPipelinePolicy redirectFlociBaseUrlPolicy() {
        return new RedirectFlociBaseUrlPolicy();
    }

    /**
     * Returns an SSL context that trusts the TLS certificate of the Floci Azure container.
     */
    protected static synchronized SSLContext sslContext() {
        if (sslContext == null) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                int index = 0;
                for (Certificate certificate : CertificateFactory.getInstance("X.509").generateCertificates(
                        new ByteArrayInputStream(floci.getTlsCertificate().getBytes(StandardCharsets.UTF_8)))) {
                    trustStore.setCertificateEntry("floci-az-" + index++, certificate);
                }

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            } catch (GeneralSecurityException | java.io.IOException e) {
                throw new IllegalStateException("Failed to trust the TLS certificate of Floci Azure", e);
            }
        }
        return sslContext;
    }

    private static final class RedirectFlociBaseUrlPolicy implements HttpPipelinePolicy {

        @Override
        public Mono<HttpResponse> process(HttpPipelineCallContext context, HttpPipelineNextPolicy next) {
            URL url = context.getHttpRequest().getUrl();
            if ("localhost".equals(url.getHost()) && url.getPort() == FlociAzContainer.PORT) {
                try {
                    URI endpoint = URI.create(floci.getHttpsEndpoint());
                    context.getHttpRequest().setUrl(
                            new URL(endpoint.getScheme(), endpoint.getHost(), endpoint.getPort(), url.getFile()));
                } catch (MalformedURLException e) {
                    return Mono.error(e);
                }
            }
            return next.process();
        }
    }
}
