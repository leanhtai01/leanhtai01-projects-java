package com.leanhtai01.archinstall.partition;

public final class PartitionLayoutMenu {
    private PartitionLayoutMenu() {
    }

    public static void displayPartitionLayoutSelectMenu() {
        System.console().printf("1. Unencrypted partition layout%n");
        System.console().printf("2. Unencrypted dual boot Windows partition layout%n");
        System.console().printf("? ");
    }
}
