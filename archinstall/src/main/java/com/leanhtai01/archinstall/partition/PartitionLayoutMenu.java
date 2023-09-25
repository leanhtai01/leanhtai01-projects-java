package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;
import com.leanhtai01.lib.InputValidation;

public final class PartitionLayoutMenu {
    private PartitionLayoutMenu() {
    }

    public static void displayPartitionLayoutSelectMenu() {
        System.console().printf("1. Unencrypted partition layout%n");
        System.console().printf("2. Unencrypted dual boot Windows partition layout%n");
        System.console().printf("3. LVM on LUKS partition layout%n");
        System.console().printf("? ");
    }

    public static PartitionLayout getPartitionLayout() throws IOException, InterruptedException {
        runVerbose(List.of("lsblk"));

        System.console().printf("Enter disk's name (e.g. nvme0n1, sda): ");
        String diskName = System.console().readLine();

        System.console().printf("Enter swap size: ");
        long swapSize = Long.parseLong(System.console().readLine());

        int choice = InputValidation.chooseIntegerOption(PartitionLayoutMenu::displayPartitionLayoutSelectMenu, 1, 3);
        PartitionLayout partitionLayout = null;

        if (choice == 1) {
            partitionLayout = new NormalPartitionLayout(diskName,
                    new StorageDeviceSize(550L, "M"), new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"));
        } else if (choice == 2) {
            partitionLayout = new NormalDualBootWindowsPartitionLayout(
                    diskName, new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"));
        } else if (choice == 3) {
            String password = InputValidation.readPasswordFromConsole(
                    "LUKS's password: ",
                    "Re-enter LUKS's password: ",
                    "Two password isn't the same. Please try again!%n");
            partitionLayout = new LVMOnLUKSPartitionLayout(diskName, new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"), password);
        }

        return partitionLayout;
    }
}
