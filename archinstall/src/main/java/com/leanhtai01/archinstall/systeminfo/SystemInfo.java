package com.leanhtai01.archinstall.systeminfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.leanhtai01.archinstall.partition.PartitionLayout;
import com.leanhtai01.archinstall.partition.PartitionLayoutMenu;
import com.leanhtai01.lib.InputValidation;

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

    public void getSystemInfo() throws IOException, InterruptedException {
        System.console().printf("Hostname: ");
        hostname = System.console().readLine();

        rootPassword = InputValidation.readPasswordFromConsole(
                "Root's password: ",
                "Re-enter root's password: ",
                "Two password isn't the same. Please try again!%n");

        PartitionLayoutMenu partitionLayoutMenu = new PartitionLayoutMenu();
        partitionLayout = partitionLayoutMenu.getPartitionLayout();

        String[] mirrorsArray = new String[3];
        Arrays.fill(mirrorsArray, "Server = https://mirror.xtom.com.hk/archlinux/$repo/os/$arch");
        mirrors = Arrays.asList(mirrorsArray);
    }
}
