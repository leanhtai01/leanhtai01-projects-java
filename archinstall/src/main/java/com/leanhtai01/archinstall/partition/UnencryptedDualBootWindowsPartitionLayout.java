package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.DiskUtil.createLinuxRootPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createSwapPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createXBOOTLDRPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.formatEXT4;
import static com.leanhtai01.archinstall.util.DiskUtil.formatFAT32;
import static com.leanhtai01.archinstall.util.DiskUtil.makeSwap;
import static com.leanhtai01.archinstall.util.DiskUtil.wipeDeviceSignature;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class UnencryptedDualBootWindowsPartitionLayout implements PartitionLayout {
    private String diskName;
    private StorageDeviceSize xbootldrSize;
    private StorageDeviceSize swapSize;

    private Partition espPartition;
    private Partition xbootldrPartition;
    private Partition rootPartition;

    public UnencryptedDualBootWindowsPartitionLayout(String diskName, StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize) {
        this.diskName = diskName;
        this.xbootldrSize = xbootldrSize;
        this.swapSize = swapSize;
    }

    @Override
    public void create() throws InterruptedException, IOException {
        espPartition = new Partition(diskName, 1, "/mnt/efi");
        xbootldrPartition = createXBOOTLDRPartition(diskName, 5, xbootldrSize, "/mnt/boot");
        var swapPartition = createSwapPartition(diskName, 6, swapSize, null);
        rootPartition = createLinuxRootPartition(diskName, 7, new StorageDeviceSize(0L, null), "/mnt");

        wipeDeviceSignature(xbootldrPartition.getPathToPartition());
        wipeDeviceSignature(swapPartition.getPathToPartition());
        wipeDeviceSignature(rootPartition.getPathToPartition());

        formatFAT32(xbootldrPartition.getPathToPartition());
        makeSwap(swapPartition.getPathToPartition());
        formatEXT4(rootPartition.getPathToPartition());
    }

    @Override
    public Partition getRootPartition() {
        return rootPartition;
    }

    @Override
    public void mount() throws InterruptedException, IOException {
        rootPartition.mount();
        espPartition.mount();
        xbootldrPartition.mount();
    }
}
