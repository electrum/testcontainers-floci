package io.floci.testcontainers.az.services;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.SyncPoller;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmailServiceTest extends AbstractServiceTest {

    @Test
    void shouldSendEmail() {
        // The Communication Services credential only signs https:// URLs
        EmailClient client = new EmailClientBuilder()
                .connectionString("endpoint=" + floci.getHttpsEndpoint() + "/;accesskey=" + floci.getAccountKey())
                .httpClient(httpsClient())
                // the polling URL is generated from Floci's base URL, not the mapped container port
                .addPolicy(redirectFlociBaseUrlPolicy())
                .buildClient();

        SyncPoller<EmailSendResult, EmailSendResult> poller = client.beginSend(new EmailMessage()
                .setSenderAddress("DoNotReply@example.com")
                .setToRecipients(List.of(new EmailAddress("dev@example.com")))
                .setSubject("Hello")
                .setBodyPlainText("Hello from testcontainers-floci-az"));
        poller.waitForCompletion();

        assertThat(poller.getFinalResult().getStatus()).isEqualTo(EmailSendStatus.SUCCEEDED);
    }
}
