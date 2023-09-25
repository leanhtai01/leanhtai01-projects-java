package com.leanhtai01.archinstall.partition;

public interface LVMOnLUKSLayout extends PartitionLayout {
    Partition getLinuxLUKSPartition();

    String getLUKSMapperName();
}
