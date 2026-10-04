package io.floci.testcontainers.az.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BlobServiceTest extends AbstractServiceTest {

    @Test
    void shouldUploadAndDownloadBlob() {
        BlobContainerClient container = new BlobServiceClientBuilder()
                .connectionString(floci.getStorageConnectionString())
                .buildClient()
                .createBlobContainer("container-" + UUID.randomUUID());
        BlobClient blob = container.getBlobClient("hello.txt");

        blob.upload(BinaryData.fromString("hello"));

        assertThat(blob.downloadContent().toString()).isEqualTo("hello");
        assertThat(container.listBlobs()).extracting(item -> item.getName()).containsExactly("hello.txt");
    }
}
