package com.leanhtai01.archinstall.partition;

public interface LVMOnLUKSPartitionLayout extends PartitionLayout {
    Partition getLinuxLUKSPartition();

    String getLUKSMapperName();
}
