package com.leanhtai01.archinstall.partition;

import static com.leanhtai01.archinstall.util.ShellUtil.runGetOutput;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public interface Mountable {
    String getMountPoint();

    String getPath();

    default void mount() throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(getMountPoint()));
        runVerbose(List.of("mount", getPath(), getMountPoint()));
    }

    default String getUUID() throws IOException {
        return runGetOutput(List.of("blkid", "-s", "UUID", "-o", "value", getPath()));
    }
}
