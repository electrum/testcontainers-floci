package io.floci.testcontainers.az.services;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QueueServiceTest extends AbstractServiceTest {

    @Test
    void shouldSendAndReceiveMessage() {
        QueueClient queue = new QueueServiceClientBuilder()
                .connectionString(floci.getStorageConnectionString())
                .buildClient()
                .createQueue("queue-" + UUID.randomUUID());

        queue.sendMessage("hello");
        List<QueueMessageItem> messages = queue.receiveMessages(1).stream().toList();

        assertThat(messages).singleElement()
                .satisfies(message -> assertThat(message.getBody().toString()).isEqualTo("hello"));
    }
}
