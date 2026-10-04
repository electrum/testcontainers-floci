package io.floci.testcontainers.az.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VmServiceTest extends AbstractServiceTest {

    private static final String API_VERSION = "?api-version=2024-11-01";

    @Test
    void shouldCreateMockedVirtualMachine() {
        // Virtual machines are mocked by default: no container is started
        String vm = createResourceGroup() + "/providers/Microsoft.Compute/virtualMachines/vm";

        RestResponse created = rest("PUT", vm + API_VERSION, """
                {
                  "location": "eastus",
                  "properties": {
                    "hardwareProfile": {"vmSize": "Standard_D2s_v3"},
                    "storageProfile": {
                      "imageReference": {"publisher": "Canonical", "offer": "0001-com-ubuntu-server-jammy", "sku": "22_04-lts", "version": "latest"},
                      "osDisk": {"createOption": "FromImage"}
                    },
                    "osProfile": {"adminUsername": "azureuser", "computerName": "vm"}
                  }
                }
                """);
        RestResponse instanceView = rest("GET", vm + "/instanceView" + API_VERSION, null);

        assertThat(created.isSuccessful()).as(created.toString()).isTrue();
        assertThat(instanceView.body()).contains("PowerState/running");
    }
}
