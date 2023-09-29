package com.leanhtai01.archinstall.systeminfo;

import java.util.List;

import com.leanhtai01.archinstall.partition.PartitionLayout;

public class SystemInfo {
    private String hostname;
    private String rootPassword;
    private List<String> mirrors;
    private PartitionLayout partitionLayout;

    public SystemInfo() {
    }

    public SystemInfo(String hostname, String rootPassword, List<String> mirrors, PartitionLayout partitionLayout) {
        this.hostname = hostname;
        this.rootPassword = rootPassword;
        this.mirrors = mirrors;
        this.partitionLayout = partitionLayout;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getRootPassword() {
        return rootPassword;
    }

    public void setRootPassword(String rootPassword) {
        this.rootPassword = rootPassword;
    }

    public List<String> getMirrors() {
        return mirrors;
    }

    public void setMirrors(List<String> mirrors) {
        this.mirrors = mirrors;
    }

    public PartitionLayout getPartitionLayout() {
        return partitionLayout;
    }

    public void setPartitionLayout(PartitionLayout partitionLayout) {
        this.partitionLayout = partitionLayout;
    }
}