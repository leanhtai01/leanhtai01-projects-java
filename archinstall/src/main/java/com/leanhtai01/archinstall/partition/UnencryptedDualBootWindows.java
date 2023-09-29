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

public class UnencryptedDualBootWindows implements PartitionLayout {
    private String diskName;
    private StorageDeviceSize xbootldrSize;
    private StorageDeviceSize swapSize;

    private Partition espPartition;
    private Partition xbootldrPartition;
    private Partition swapPartition;
    private Partition rootPartition;

    public UnencryptedDualBootWindows(String diskName, StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize) {
        this.diskName = diskName;
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

    @Override
    public void create() throws InterruptedException, IOException {
        espPartition = new Partition(diskName, 1, "/mnt/efi");
        xbootldrPartition = createXBOOTLDRPartition(diskName, 5, xbootldrSize, "/mnt/boot");
        swapPartition = createSwapPartition(diskName, 6, swapSize, null);
        rootPartition = createLinuxRootPartition(diskName, 7, null, "/mnt");

        wipeDeviceSignature(xbootldrPartition.getPath());
        wipeDeviceSignature(swapPartition.getPath());
        wipeDeviceSignature(rootPartition.getPath());

        formatFAT32(xbootldrPartition.getPath());
        makeSwap(swapPartition.getPath());
        formatEXT4(rootPartition.getPath());
    }

    @Override
    public void mount() throws InterruptedException, IOException {
        rootPartition.mount();
        espPartition.mount();
        xbootldrPartition.mount();
    }
}
