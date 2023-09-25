package com.leanhtai01.archinstall.util;

import static com.leanhtai01.archinstall.util.ShellUtil.runSetInput;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.leanhtai01.archinstall.partition.LogicalVolume;
import com.leanhtai01.archinstall.partition.Partition;
import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public final class DiskUtil {
    private DiskUtil() {
    }

    public static void eraseDisk(String pathToDisk) throws InterruptedException, IOException {
        runVerbose(List.of("wipefs", "-a", pathToDisk));
        runVerbose(List.of("sgdisk", "-Z", pathToDisk));
        TimeUnit.SECONDS.sleep(2);
    }

    public static void wipeDeviceSignature(String pathToDevice) throws InterruptedException, IOException {
        runVerbose(List.of("wipefs", "-a", pathToDevice));
    }

    public static Partition createPartition(Partition partition)
            throws InterruptedException, IOException {
        runVerbose(List.of("sgdisk",
                "-n", "0:0:" + (partition.getSize() == null ? "0"
                        : "+%d%s".formatted(partition.getSize().getValue(), partition.getSize().getUnit())),
                "-t", "0:%s".formatted(partition.getType()),
                "-c", "0:%s".formatted(partition.getGptName()),
                partition.getPathToDisk()));

        return partition;
    }

    public static Partition createEFIPartition(
            String diskName,
            int partitionNumber,
            StorageDeviceSize size,
            String mountPoint)
            throws InterruptedException, IOException {
        return createPartition(new Partition(diskName, partitionNumber, "ef00", "esp", size, mountPoint));
    }

    public static Partition createXBOOTLDRPartition(
            String diskName,
            int partitionNumber,
            StorageDeviceSize size,
            String mountPoint)
            throws InterruptedException, IOException {
        return createPartition(new Partition(diskName, partitionNumber, "ea00", "XBOOTLDR", size, mountPoint));
    }

    public static Partition createSwapPartition(
            String diskName,
            int partitionNumber,
            StorageDeviceSize size,
            String mountPoint)
            throws InterruptedException, IOException {
        return createPartition(new Partition(diskName, partitionNumber, "8200", "swap", size, mountPoint));
    }

    public static Partition createLinuxRootPartition(
            String diskName,
            int partitionNumber,
            StorageDeviceSize size,
            String mountPoint)
            throws InterruptedException, IOException {
        return createPartition(new Partition(diskName, partitionNumber, "8304", "root", size, mountPoint));
    }

    public static Partition createLinuxLUKSPartition(String diskName, int partitionNumber, StorageDeviceSize size)
            throws InterruptedException, IOException {
        return createPartition(new Partition(diskName, partitionNumber, "8309", "luks-encrypted", size, null));
    }

    public static void createLVMPhysicalVolume(String pathToDevice) throws IOException, InterruptedException {
        runVerbose(List.of("pvcreate", pathToDevice));
    }

    public static void createLVMVolumeGroup(String pathToDevice, String vgName)
            throws IOException, InterruptedException {
        runVerbose(List.of("vgcreate", vgName, pathToDevice));
    }

    public static LogicalVolume createLVMLogicalVolume(LogicalVolume logicalVolume)
            throws IOException, InterruptedException {
        runVerbose(List.of("lvcreate", logicalVolume.getSize() != null ? "-L" : "-l",
                logicalVolume.getSize() != null
                        ? "%d%s".formatted(logicalVolume.getSize().getValue(), logicalVolume.getSize().getUnit())
                        : "+100%FREE",
                logicalVolume.getVgName(), "-n", logicalVolume.getLvName()));

        return logicalVolume;
    }

    public static Partition createLUKSContainer(Partition partition, String password)
            throws IOException, InterruptedException {
        runSetInput(List.of("cryptsetup", "luksFormat", "--type", "luks2", partition.getPath()),
                List.of(password));

        return partition;
    }

    public static Partition openLUKSContainer(Partition partition, String luksMapperName, String password)
            throws IOException, InterruptedException {
        runSetInput(List.of("cryptsetup", "open", partition.getPath(), luksMapperName), List.of(password));

        return partition;
    }

    public static void makeSwap(String pathToDevice) throws InterruptedException, IOException {
        runVerbose(List.of("mkswap", pathToDevice));
        runVerbose(List.of("swapon", pathToDevice));
    }

    public static void formatFAT32(String pathToDevice) throws InterruptedException, IOException {
        runVerbose(List.of("mkfs.vfat", "-F32", pathToDevice));
    }

    public static void formatEXT4(String pathToDevice) throws InterruptedException, IOException {
        runVerbose(List.of("mkfs.ext4", pathToDevice));
    }

    public static String getPathToDisk(String diskName) {
        return "/dev/%s".formatted(diskName);
    }
}
