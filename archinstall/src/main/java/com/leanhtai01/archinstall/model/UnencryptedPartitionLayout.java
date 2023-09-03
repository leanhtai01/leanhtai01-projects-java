package com.leanhtai01.archinstall.model;

import java.io.IOException;

import com.leanhtai01.archinstall.util.DiskUtil;

public class UnencryptedPartitionLayout implements PartitionLayout {
    private String diskName;
    private StorageDeviceSize espSize;
    private StorageDeviceSize xbootldrSize;
    private StorageDeviceSize swapSize;

    private Partition espPartition;
    private Partition xbootldrPartition;
    private Partition rootPartition;

    public UnencryptedPartitionLayout(
            String diskName,
            StorageDeviceSize espSize,
            StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize) {
        this.diskName = diskName;
        this.espSize = espSize;
        this.xbootldrSize = xbootldrSize;
        this.swapSize = swapSize;
    }

    public Partition getRootPartition() {
        return rootPartition;
    }

    public void create() throws InterruptedException, IOException {
        DiskUtil.eraseDisk(DiskUtil.getPathToDisk(diskName));

        espPartition = DiskUtil.createEFIPartition(diskName, 1, espSize, "/mnt/efi");
        xbootldrPartition = DiskUtil.createXBOOTLDRPartition(diskName, 2, xbootldrSize, "/mnt/boot");
        var swapPartition = DiskUtil.createSwapPartition(diskName, 3, swapSize, null);
        rootPartition = DiskUtil.createLinuxRootPartition(diskName, 4, new StorageDeviceSize(0L, null), "/mnt");

        DiskUtil.wipeDeviceSignature(espPartition.getPathToPartition());
        DiskUtil.wipeDeviceSignature(xbootldrPartition.getPathToPartition());
        DiskUtil.wipeDeviceSignature(swapPartition.getPathToPartition());
        DiskUtil.wipeDeviceSignature(rootPartition.getPathToPartition());

        DiskUtil.formatFAT32(espPartition.getPathToPartition());
        DiskUtil.formatFAT32(xbootldrPartition.getPathToPartition());
        DiskUtil.makeSwap(swapPartition.getPathToPartition());
        DiskUtil.formatEXT4(rootPartition.getPathToPartition());
    }

    public void mount() throws InterruptedException, IOException {
        rootPartition.mount();
        espPartition.mount();
        xbootldrPartition.mount();
    }
}
