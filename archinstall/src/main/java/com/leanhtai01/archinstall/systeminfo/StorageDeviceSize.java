package com.leanhtai01.archinstall.systeminfo;

public class StorageDeviceSize {
    private Long value;
    private String unit;

    public StorageDeviceSize(Long value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
