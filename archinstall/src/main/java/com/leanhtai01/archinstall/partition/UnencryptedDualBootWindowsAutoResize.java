package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.DiskUtil.shrinkNTFSPartition;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class UnencryptedDualBootWindowsAutoResize extends UnencryptedDualBootWindows {
    private Partition windowsPartition;
    private StorageDeviceSize linuxSystemSize;

    public UnencryptedDualBootWindowsAutoResize(
            String diskName,
            StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize,
            Partition windowsPartition,
            StorageDeviceSize linuxSystemSize) {
        super(diskName, xbootldrSize, swapSize);
        this.windowsPartition = windowsPartition;
        this.linuxSystemSize = linuxSystemSize;
    }

    @Override
    public void create() throws InterruptedException, IOException {
        shrinkNTFSPartition(windowsPartition, linuxSystemSize);
        super.create();
    }
}
