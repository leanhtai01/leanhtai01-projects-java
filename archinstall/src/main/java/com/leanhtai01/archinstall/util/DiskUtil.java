package com.leanhtai01.archinstall.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.leanhtai01.archinstall.partition.Partition;
import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public final class DiskUtil {
    private DiskUtil() {
    }

    public static void eraseDisk(String pathToDisk) throws InterruptedException, IOException {
        new ProcessBuilder("wipefs", "-a", pathToDisk).inheritIO().start().waitFor();
        new ProcessBuilder("sgdisk", "-Z", pathToDisk).inheritIO().start().waitFor();
        TimeUnit.SECONDS.sleep(2);
    }

    public static void wipeDeviceSignature(String pathToDevice) throws InterruptedException, IOException {
        new ProcessBuilder("wipefs", "-a", pathToDevice).inheritIO().start().waitFor();
    }

    public static Partition createPartition(Partition partition)
            throws InterruptedException, IOException {
        new ProcessBuilder("sgdisk",
                "-n", "0:0:" + (partition.getSize().getValue() == 0L ? "0"
                        : "+%d%s".formatted(partition.getSize().getValue(), partition.getSize().getUnit())),
                "-t", "0:%s".formatted(partition.getType()),
                "-c", "0:%s".formatted(partition.getGptName()),
                partition.getPathToDisk())
                .inheritIO().start().waitFor();

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

    public static void makeSwap(String pathToDevice) throws InterruptedException, IOException {
        new ProcessBuilder("mkswap", pathToDevice).inheritIO().start().waitFor();
        new ProcessBuilder("swapon", pathToDevice).inheritIO().start().waitFor();
    }

    public static void formatFAT32(String pathToDevice) throws InterruptedException, IOException {
        new ProcessBuilder("mkfs.vfat", "-F32", pathToDevice).inheritIO().start().waitFor();
    }

    public static void formatEXT4(String pathToDevice) throws InterruptedException, IOException {
        new ProcessBuilder("mkfs.ext4", pathToDevice).inheritIO().start().waitFor();
    }

    public static String getPathToDisk(String diskName) {
        return "/dev/%s".formatted(diskName);
    }
}
