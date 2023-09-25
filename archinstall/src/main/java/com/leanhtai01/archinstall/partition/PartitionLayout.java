package com.leanhtai01.archinstall.partition;

import java.io.IOException;

public interface PartitionLayout {
    Mountable getRoot();

    Mountable getSwap();

    void create() throws InterruptedException, IOException;

    void mount() throws InterruptedException, IOException;
}
