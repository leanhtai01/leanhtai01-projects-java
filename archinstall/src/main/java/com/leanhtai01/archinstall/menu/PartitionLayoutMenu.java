package com.leanhtai01.archinstall.menu;

import static com.leanhtai01.archinstall.util.IOUtil.readPassword;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import com.leanhtai01.archinstall.partition.LVMOnLUKS;
import com.leanhtai01.archinstall.partition.LVMOnLUKSDualBootWindows;
import com.leanhtai01.archinstall.partition.Partition;
import com.leanhtai01.archinstall.partition.PartitionLayout;
import com.leanhtai01.archinstall.partition.Unencrypted;
import com.leanhtai01.archinstall.partition.UnencryptedDualBootWindows;
import com.leanhtai01.archinstall.partition.UnencryptedDualBootWindowsAutoResize;
import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class PartitionLayoutMenu extends SingleChoiceMenu {
    private static final StorageDeviceSize ESP_SIZE = new StorageDeviceSize(BigInteger.valueOf(550L), "M");
    private static final StorageDeviceSize XBOOTLDR_SIZE = new StorageDeviceSize(BigInteger.valueOf(550L), "M");

    private PartitionLayout partitionLayout;

    public PartitionLayoutMenu() throws IOException, InterruptedException {
        super();

        runVerbose(List.of("lsblk"));
        System.console().printf("Enter disk's name (e.g. nvme0n1, sda): ");
        String diskName = System.console().readLine();

        System.console().printf("Enter swap size: ");
        long swapSizeInput = Long.parseLong(System.console().readLine());
        final StorageDeviceSize swapSize = new StorageDeviceSize(BigInteger.valueOf(swapSizeInput), "G");

        Runnable setUnencrypted = () -> partitionLayout = new Unencrypted(diskName, ESP_SIZE, XBOOTLDR_SIZE, swapSize);

        Runnable setUnencryptedDualBootWindows = () -> partitionLayout = new UnencryptedDualBootWindows(
                diskName, XBOOTLDR_SIZE, swapSize);

        Runnable setUnencryptedDualBootWindowsAutoResize = () -> {
            System.console().printf("Enter Windows's partition number: ");
            int partitionNumber = Integer.parseInt(System.console().readLine());

            System.console().printf("Enter Linux's system size in GiB: ");
            StorageDeviceSize linuxSystemSize = new StorageDeviceSize(
                    BigInteger.valueOf(Long.parseLong(System.console().readLine())), "G");

            Partition windowsPartition = new Partition(diskName, partitionNumber);
            partitionLayout = new UnencryptedDualBootWindowsAutoResize(diskName, XBOOTLDR_SIZE, swapSize,
                    windowsPartition, linuxSystemSize);
        };

        Runnable setLVMOnLUKS = () -> {
            String password = getLUKSPassword();
            partitionLayout = new LVMOnLUKS(diskName, ESP_SIZE, XBOOTLDR_SIZE, swapSize, password);
        };
        Runnable setLVMOnLUKSDualBootWindows = () -> {
            String password = getLUKSPassword();
            partitionLayout = new LVMOnLUKSDualBootWindows(diskName, XBOOTLDR_SIZE, swapSize, password);
        };

        addOption(new Option("Unencrypted partition layout", setUnencrypted, false));
        addOption(new Option("Unencrypted dual boot Windows partition layout", setUnencryptedDualBootWindows, false));
        addOption(new Option("Unencrypted dual boot Windows partition layout (auto resize)",
                setUnencryptedDualBootWindowsAutoResize, false));
        addOption(new Option("LVM on LUKS partition layout", setLVMOnLUKS, false));
        addOption(new Option("LVM on LUKS dual boot Windows partition layout", setLVMOnLUKSDualBootWindows, false));
    }

    public PartitionLayout selectPartitionLayout() {
        selectOption();
        doAction();
        return partitionLayout;
    }

    private String getLUKSPassword() {
        return readPassword(
                "LUKS's password: ",
                "Re-enter LUKS's password: ");
    }
}
