package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.DiskUtil.convertGibibyteToByte;
import static com.leanhtai01.archinstall.util.DiskUtil.shrinkNTFSPartition;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class LVMOnLUKSDualBootWindowsAutoResize extends LVMOnLUKSDualBootWindows {
    private Partition windowsPartition;
    private StorageDeviceSize linuxSystemSize;

    public LVMOnLUKSDualBootWindowsAutoResize(
            String diskName,
            StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize,
            String password,
            Partition windowsPartition,
            StorageDeviceSize linuxSystemSize) {
        super(diskName, xbootldrSize, swapSize, password);
        this.windowsPartition = windowsPartition;
        this.linuxSystemSize = linuxSystemSize;
    }

    @Override
    public void create() throws InterruptedException, IOException {
        shrinkNTFSPartition(windowsPartition, convertGibibyteToByte(linuxSystemSize));
        super.create();
    }
}
