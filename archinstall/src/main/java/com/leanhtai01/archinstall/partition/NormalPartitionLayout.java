package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.DiskUtil.createEFIPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createLinuxRootPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createSwapPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createXBOOTLDRPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.eraseDisk;
import static com.leanhtai01.archinstall.util.DiskUtil.formatEXT4;
import static com.leanhtai01.archinstall.util.DiskUtil.formatFAT32;
import static com.leanhtai01.archinstall.util.DiskUtil.getPathToDisk;
import static com.leanhtai01.archinstall.util.DiskUtil.makeSwap;
import static com.leanhtai01.archinstall.util.DiskUtil.wipeDeviceSignature;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class NormalPartitionLayout implements PartitionLayout {
    private String diskName;
    private StorageDeviceSize espSize;
    private StorageDeviceSize xbootldrSize;
    private StorageDeviceSize swapSize;

    private Partition espPartition;
    private Partition xbootldrPartition;
    private Partition swapPartition;
    private Partition rootPartition;

    public NormalPartitionLayout(
            String diskName,
            StorageDeviceSize espSize,
            StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize) {
        this.diskName = diskName;
        this.espSize = espSize;
        this.xbootldrSize = xbootldrSize;
        this.swapSize = swapSize;
    }

    @Override
    public Partition getRoot() {
        return rootPartition;
    }

    @Override
    public Partition getSwap() {
        return swapPartition;
    }

    public void create() throws InterruptedException, IOException {
        eraseDisk(getPathToDisk(diskName));

        espPartition = createEFIPartition(diskName, 1, espSize, "/mnt/efi");
        xbootldrPartition = createXBOOTLDRPartition(diskName, 2, xbootldrSize, "/mnt/boot");
        swapPartition = createSwapPartition(diskName, 3, swapSize, null);
        rootPartition = createLinuxRootPartition(diskName, 4, null, "/mnt");

        wipeDeviceSignature(espPartition.getPath());
        wipeDeviceSignature(xbootldrPartition.getPath());
        wipeDeviceSignature(swapPartition.getPath());
        wipeDeviceSignature(rootPartition.getPath());

        formatFAT32(espPartition.getPath());
        formatFAT32(xbootldrPartition.getPath());
        makeSwap(swapPartition.getPath());
        formatEXT4(rootPartition.getPath());
    }

    public void mount() throws InterruptedException, IOException {
        rootPartition.mount();
        espPartition.mount();
        xbootldrPartition.mount();
    }
}
