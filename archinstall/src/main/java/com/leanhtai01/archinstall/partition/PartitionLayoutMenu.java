package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;
import static com.leanhtai01.lib.InputValidation.*;

public final class PartitionLayoutMenu {
    private List<String> menu;

    public PartitionLayoutMenu() {
        menu = new ArrayList<>();
        menu.add("Normal partition layout");
        menu.add("Normal dual boot Windows partition layout");
        menu.add("LVM on LUKS partition layout");
        menu.add("LVM on LUKS dual boot Windows partition layout");
    }

    public PartitionLayout getPartitionLayout() throws IOException, InterruptedException {
        runVerbose(List.of("lsblk"));

        System.console().printf("Enter disk's name (e.g. nvme0n1, sda): ");
        String diskName = System.console().readLine();

        System.console().printf("Enter swap size: ");
        long swapSize = Long.parseLong(System.console().readLine());

        int choice = chooseIntegerOption(() -> displayMenu(menu, "? "), 1, menu.size(), -1);
        PartitionLayout partitionLayout = null;

        switch (choice) {
            case 1 -> partitionLayout = new NormalPartitionLayout(
                    diskName,
                    new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"));
            case 2 -> partitionLayout = new NormalDualBootWindowsPartitionLayout(
                    diskName,
                    new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"));
            case 3 -> {
                String password = getLUKSPassword();
                partitionLayout = new LVMOnLUKSPartitionLayout(
                        diskName,
                        new StorageDeviceSize(550L, "M"),
                        new StorageDeviceSize(550L, "M"),
                        new StorageDeviceSize(swapSize, "G"), password);
            }
            case 4 -> {
                String password = getLUKSPassword();
                partitionLayout = new LVMOnLUKSDualBootWindowsPartitionLayout(
                        diskName,
                        new StorageDeviceSize(550L, "M"),
                        new StorageDeviceSize(swapSize, "G"), password);
            }
            default -> partitionLayout = null;
        }

        return partitionLayout;
    }

    private String getLUKSPassword() {
        return readPasswordFromConsole(
                "LUKS's password: ",
                "Re-enter LUKS's password: ",
                "Two password isn't the same. Please try again!%n");
    }
}
