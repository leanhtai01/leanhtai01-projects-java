package com.leanhtai01.archinstall.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.leanhtai01.archinstall.util.ShellUtil;

public class Partition {
    private String diskName;
    private int partitionNumber;
    private String type;
    private String gptName;
    private StorageDeviceSize size;
    private String mountPoint;

    public Partition(String diskName, int partitionNumber) {
        this.diskName = diskName;
        this.partitionNumber = partitionNumber;
    }

    public Partition(String type, String gptName, StorageDeviceSize size) {
        this.type = type;
        this.gptName = gptName;
        this.size = size;
    }

    public Partition(
            String diskName,
            int partitionNumber,
            String type,
            String gptName,
            StorageDeviceSize size,
            String mountPoint) {
        this.diskName = diskName;
        this.partitionNumber = partitionNumber;
        this.type = type;
        this.gptName = gptName;
        this.size = size;
        this.mountPoint = mountPoint;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public int getPartitionNumber() {
        return partitionNumber;
    }

    public void setPartitionNumber(int partitionNumber) {
        this.partitionNumber = partitionNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGptName() {
        return gptName;
    }

    public void setGptName(String gptName) {
        this.gptName = gptName;
    }

    public StorageDeviceSize getSize() {
        return size;
    }

    public void setSize(StorageDeviceSize size) {
        this.size = size;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public String getPathToDisk() {
        return "/dev/%s".formatted(diskName);
    }

    public String getPathToPartition() {
        return diskName.startsWith("nvme") ? "%sp%d".formatted(getPathToDisk(), partitionNumber)
                : getPathToDisk() + partitionNumber;
    }

    public void mount() throws InterruptedException, IOException {
        Files.createDirectories(Paths.get(mountPoint));
        ShellUtil.run("mount", getPathToPartition(), mountPoint);
    }
}
