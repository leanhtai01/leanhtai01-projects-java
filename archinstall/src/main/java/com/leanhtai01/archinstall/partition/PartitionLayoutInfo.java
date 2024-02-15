package com.leanhtai01.archinstall.partition;

import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PartitionLayoutInfo {
    private String diskName;
    private StorageDeviceSize swapSize;
    private StorageDeviceSize rootSize;
    private String password;
    private Partition windowsPartition;
    private int option;
}
