package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.DiskUtil.createLUKSContainer;
import static com.leanhtai01.archinstall.util.DiskUtil.createLVMLogicalVolume;
import static com.leanhtai01.archinstall.util.DiskUtil.createLVMPhysicalVolume;
import static com.leanhtai01.archinstall.util.DiskUtil.createLVMVolumeGroup;
import static com.leanhtai01.archinstall.util.DiskUtil.createLinuxLUKSPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.createXBOOTLDRPartition;
import static com.leanhtai01.archinstall.util.DiskUtil.formatEXT4;
import static com.leanhtai01.archinstall.util.DiskUtil.formatFAT32;
import static com.leanhtai01.archinstall.util.DiskUtil.makeSwap;
import static com.leanhtai01.archinstall.util.DiskUtil.openLUKSContainer;
import static com.leanhtai01.archinstall.util.DiskUtil.wipeDeviceSignature;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class LVMOnLUKSDualBootWindowsPartitionLayout implements LVMOnLUKSLayout {
    private String diskName;
    private StorageDeviceSize xbootldrSize;
    private StorageDeviceSize swapSize;

    private Partition espPartition;
    private Partition xbootldrPartition;
    private Partition linuxLUKSPartition;

    private static final String LUKS_MAPPER_NAME = "encrypt-lvm";
    private static final String VG_NAME = "vg-system";
    private static final String LUKS_MAPPER_DEVICE_PATH = "/dev/mapper/%s".formatted(LUKS_MAPPER_NAME);
    private LogicalVolume swapVolume;
    private LogicalVolume rootVolume;
    private String password;

    public LVMOnLUKSDualBootWindowsPartitionLayout(
            String diskName,
            StorageDeviceSize xbootldrSize,
            StorageDeviceSize swapSize,
            String password) {
        this.diskName = diskName;
        this.xbootldrSize = xbootldrSize;
        this.swapSize = swapSize;
        this.password = password;
    }

    @Override
    public String getLUKSMapperName() {
        return LUKS_MAPPER_NAME;
    }

    @Override
    public Partition getLinuxLUKSPartition() {
        return linuxLUKSPartition;
    }

    @Override
    public LogicalVolume getRoot() {
        return rootVolume;
    }

    @Override
    public LogicalVolume getSwap() {
        return swapVolume;
    }

    @Override
    public void create() throws InterruptedException, IOException {
        espPartition = new Partition(diskName, 1, "/mnt/efi");
        xbootldrPartition = createXBOOTLDRPartition(diskName, 5, xbootldrSize, "/mnt/boot");
        linuxLUKSPartition = createLinuxLUKSPartition(diskName, 6, null);
        swapVolume = new LogicalVolume(VG_NAME, "swap", swapSize, null);
        rootVolume = new LogicalVolume(VG_NAME, "root", null, "/mnt");

        wipeDeviceSignature(xbootldrPartition.getPath());
        wipeDeviceSignature(linuxLUKSPartition.getPath());

        createLUKSContainer(linuxLUKSPartition, password);
        openLUKSContainer(linuxLUKSPartition, LUKS_MAPPER_NAME, password);
        wipeDeviceSignature(LUKS_MAPPER_DEVICE_PATH);

        createLVMPhysicalVolume(LUKS_MAPPER_DEVICE_PATH);
        createLVMVolumeGroup(LUKS_MAPPER_DEVICE_PATH, VG_NAME);
        createLVMLogicalVolume(swapVolume);
        createLVMLogicalVolume(rootVolume);

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
