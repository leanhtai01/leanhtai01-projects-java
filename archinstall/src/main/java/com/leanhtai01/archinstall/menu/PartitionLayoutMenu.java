package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.partition.LVMOnLUKS;
import com.leanhtai01.archinstall.partition.LVMOnLUKSDualBootWindows;
import com.leanhtai01.archinstall.partition.PartitionLayout;
import com.leanhtai01.archinstall.partition.Unencrypted;
import com.leanhtai01.archinstall.partition.UnencryptedDualBootWindows;
import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;
import com.leanhtai01.archinstall.util.IOUtil;

public class PartitionLayoutMenu extends SingleChoiceMenu {
    private PartitionLayout partitionLayout;

    public PartitionLayoutMenu() {
        super();

        System.console().printf("Enter disk's name (e.g. nvme0n1, sda): ");
        String diskName = System.console().readLine();

        System.console().printf("Enter swap size: ");
        long swapSize = Long.parseLong(System.console().readLine());

        Runnable setUnencrypted = () -> partitionLayout = new Unencrypted(diskName, new StorageDeviceSize(550L, "M"),
                new StorageDeviceSize(550L, "M"), new StorageDeviceSize(swapSize, "G"));
        Runnable setUnencryptedDualBootWindows = () -> partitionLayout = new UnencryptedDualBootWindows(diskName,
                new StorageDeviceSize(550L, "M"), new StorageDeviceSize(swapSize, "G"));
        Runnable setLVMOnLUKS = () -> {
            String password = getLUKSPassword();
            partitionLayout = new LVMOnLUKS(diskName, new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(550L, "M"), new StorageDeviceSize(swapSize, "G"), password);
        };
        Runnable setLVMOnLUKSDualBootWindows = () -> {
            String password = getLUKSPassword();
            partitionLayout = new LVMOnLUKSDualBootWindows(diskName, new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(swapSize, "G"), password);
        };

        addOption(new Option("Unencrypted partition layout", setUnencrypted, false));
        addOption(new Option("Unencrypted dual boot Windows partition layout", setUnencryptedDualBootWindows, false));
        addOption(new Option("LVM on LUKS partition layout", setLVMOnLUKS, false));
        addOption(new Option("LVM on LUKS dual boot Windows partition layout", setLVMOnLUKSDualBootWindows, false));
    }

    public PartitionLayout selectPartitionLayout() {
        selectOption();
        doAction();
        return partitionLayout;
    }

    private String getLUKSPassword() {
        return IOUtil.readPassword(
                "LUKS's password: ",
                "Re-enter LUKS's password: ");
    }
}
