package com.leanhtai01.archinstall.partition;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;
import com.leanhtai01.lib.InputValidation;

public final class PartitionLayoutMenu {
    private PartitionLayoutMenu() {
    }

    public static void displayPartitionLayoutSelectMenu() {
        System.console().printf("1. Unencrypted partition layout%n");
        System.console().printf("2. Unencrypted dual boot Windows partition layout%n");
        System.console().printf("? ");
    }

    public static PartitionLayout getPartitionLayout() {
        System.console().printf("Enter disk's name (e.g. nvme0n1, sda): ");
        String diskName = System.console().readLine();

        System.console().printf("Enter swap size: ");
        long swapSize = Long.parseLong(System.console().readLine());

        int choice = InputValidation.chooseIntegerOption(PartitionLayoutMenu::displayPartitionLayoutSelectMenu, 1, 2);
        PartitionLayout partitionLayout = null;

        if (choice == 1) {
            partitionLayout = new UnencryptedPartitionLayout(diskName,
                    new StorageDeviceSize(550L, "M"), new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"));
        } else if (choice == 2) {
            partitionLayout = new UnencryptedDualBootWindowsPartitionLayout(
                    diskName, new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"));
        }

        return partitionLayout;
    }
}
