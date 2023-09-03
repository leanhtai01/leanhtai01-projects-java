package com.leanhtai01.archinstall.model;

import java.io.IOException;

public interface PartitionLayout {
    void create() throws InterruptedException, IOException;

    void mount() throws InterruptedException, IOException;
}
