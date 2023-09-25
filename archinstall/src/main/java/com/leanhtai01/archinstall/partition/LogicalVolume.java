package com.leanhtai01.archinstall.partition;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

public class LogicalVolume implements Mountable {
    private String vgName;
    private String lvName;
    private String mountPoint;
    private StorageDeviceSize size;

    public LogicalVolume(String vgName, String lvName, StorageDeviceSize size, String mountPoint) {
        this.vgName = vgName;
        this.lvName = lvName;
        this.size = size;
        this.mountPoint = mountPoint;
    }

    public String getVgName() {
        return vgName;
    }

    public void setVgName(String vgName) {
        this.vgName = vgName;
    }

    public String getLvName() {
        return lvName;
    }

    public void setLvName(String lvName) {
        this.lvName = lvName;
    }

    public StorageDeviceSize getSize() {
        return size;
    }

    public void setSize(StorageDeviceSize size) {
        this.size = size;
    }

    @Override
    public String getMountPoint() {
        return mountPoint;
    }

    @Override
    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public String getPath() {
        return "/dev/%s/%s".formatted(vgName, lvName);
    }
}
