package io.floci.testcontainers.az.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.TableEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TableServiceTest extends AbstractServiceTest {

    @Test
    void shouldUpsertAndGetEntity() {
        TableClient table = new TableServiceClientBuilder()
                .connectionString(floci.getStorageConnectionString())
                .buildClient()
                .createTable("table" + UUID.randomUUID().toString().replace("-", ""));

        table.upsertEntity(new TableEntity("partition", "row").addProperty("greeting", "hello"));
        TableEntity entity = table.getEntity("partition", "row");

        assertThat(entity.getProperty("greeting")).isEqualTo("hello");
    }
}
