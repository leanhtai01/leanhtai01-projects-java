package com.leanhtai01.archinstall.model;

import java.io.IOException;

import com.leanhtai01.archinstall.util.DiskUtil;

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
        xbootldrPartition = DiskUtil.createXBOOTLDRPartition(diskName, 5, xbootldrSize, "/mnt/boot");
        var swapPartition = DiskUtil.createSwapPartition(diskName, 6, swapSize, null);
        rootPartition = DiskUtil.createLinuxRootPartition(diskName, 7, new StorageDeviceSize(0L, null), "/mnt");

        DiskUtil.wipeDeviceSignature(xbootldrPartition.getPathToPartition());
        DiskUtil.wipeDeviceSignature(swapPartition.getPathToPartition());
        DiskUtil.wipeDeviceSignature(rootPartition.getPathToPartition());

        DiskUtil.formatFAT32(xbootldrPartition.getPathToPartition());
        DiskUtil.makeSwap(swapPartition.getPathToPartition());
        DiskUtil.formatEXT4(rootPartition.getPathToPartition());
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
