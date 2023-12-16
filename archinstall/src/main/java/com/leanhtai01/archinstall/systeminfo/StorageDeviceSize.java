package com.leanhtai01.archinstall.systeminfo;

import java.math.BigInteger;

public class StorageDeviceSize {
    private BigInteger value;
    private String unit;

    public StorageDeviceSize(BigInteger value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public String getValue() {
        return value.toString();
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
