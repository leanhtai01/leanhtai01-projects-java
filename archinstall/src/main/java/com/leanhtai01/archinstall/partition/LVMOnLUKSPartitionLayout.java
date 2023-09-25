package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.DiskUtil.createEFIPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createLUKSContainer;
import static com.leanhtai01.archinstall.util.DiskUtil.createLVMLogicalVolume;
import static com.leanhtai01.archinstall.util.DiskUtil.createLVMPhysicalVolume;
import static com.leanhtai01.archinstall.util.DiskUtil.createLVMVolumeGroup;
import static com.leanhtai01.archinstall.util.DiskUtil.createLinuxLUKSPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createXBOOTLDRPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.eraseDisk;
import static com.leanhtai01.archinstall.util.DiskUtil.formatEXT4;
import static com.leanhtai01.archinstall.util.DiskUtil.formatFAT32;
import static com.leanhtai01.archinstall.util.DiskUtil.getPathToDisk;
import static com.leanhtai01.archinstall.util.DiskUtil.makeSwap;
import static com.leanhtai01.archinstall.util.DiskUtil.openLUKSContainer;
import static com.leanhtai01.archinstall.util.DiskUtil.wipeDeviceSignature;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class LVMOnLUKSPartitionLayout implements PartitionLayout {
    private String diskName;
    private StorageDeviceSize espSize;
    private StorageDeviceSize xbootldrSize;
    private StorageDeviceSize swapSize;

    private Partition espPartition;
    private Partition xbootldrPartition;
    private Partition linuxLUKSPartition;

    private static final String LUKS_MAPPER_NAME = "encrypt-lvm";
    private static final String VG_NAME = "vg-system";
    private static final String LUKS_MAPPER_DEVICE_PATH = "/dev/mapper/%s".formatted(LUKS_MAPPER_NAME);
    private LogicalVolume rootVolume;
    private String password;

    public LVMOnLUKSPartitionLayout(
            String diskName,
            StorageDeviceSize espSize,
            StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize,
            String password) {
        this.diskName = diskName;
        this.espSize = espSize;
        this.xbootldrSize = xbootldrSize;
        this.swapSize = swapSize;
        this.password = password;
    }

    @Override
    public Partition getRootPartition() {
        return linuxLUKSPartition;
    }

    @Override
    public void create() throws InterruptedException, IOException {
        eraseDisk(getPathToDisk(diskName));

        espPartition = createEFIPartition(diskName, 1, espSize, "/mnt/efi");
        xbootldrPartition = createXBOOTLDRPartition(diskName, 2, xbootldrSize, "/mnt/boot");
        linuxLUKSPartition = createLinuxLUKSPartition(diskName, 3, null);
        LogicalVolume swapVolume = new LogicalVolume(VG_NAME, "swap", swapSize, null);
        rootVolume = new LogicalVolume(VG_NAME, "root", null, "/mnt");

        wipeDeviceSignature(espPartition.getPath());
        wipeDeviceSignature(xbootldrPartition.getPath());
        wipeDeviceSignature(linuxLUKSPartition.getPath());

        createLUKSContainer(linuxLUKSPartition, password);
        openLUKSContainer(linuxLUKSPartition, LUKS_MAPPER_NAME, password);
        wipeDeviceSignature(LUKS_MAPPER_DEVICE_PATH);

        createLVMPhysicalVolume(LUKS_MAPPER_DEVICE_PATH);
        createLVMVolumeGroup(LUKS_MAPPER_DEVICE_PATH, VG_NAME);
        createLVMLogicalVolume(swapVolume);
        createLVMLogicalVolume(rootVolume);

        formatFAT32(espPartition.getPath());
        formatFAT32(xbootldrPartition.getPath());
        makeSwap(swapVolume.getPath());
        formatEXT4(rootVolume.getPath());
    }

    @Override
    public void mount() throws InterruptedException, IOException {
        rootVolume.mount();
        espPartition.mount();
        xbootldrPartition.mount();
    }
}
