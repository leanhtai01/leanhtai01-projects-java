package com.leanhtai01.archinstall.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

public final class ConfigUtil {
    private ConfigUtil() {
    }

    public static int addUserToGroup(String username, String group, String chrootDir)
            throws InterruptedException, IOException {
        List<String> command = List.of("gpasswd", "-a", username, group);
        return ShellUtil.runVerbose(chrootDir != null ? ShellUtil.getCommandRunChroot(command, chrootDir) : command);
    }

    public static void backupFile(String path) throws IOException {
        Files.copy(Paths.get(path), Paths.get(path + "_" + LocalDateTime.now()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static int startService(String service, String chrootDir) throws InterruptedException, IOException {
        return manageSystemService("start", service, chrootDir);
    }

    public static int stopService(String service, String chrootDir) throws InterruptedException, IOException {
        return manageSystemService("stop", service, chrootDir);
    }

    public static int enableService(String service, String chrootDir) throws InterruptedException, IOException {
        return manageSystemService("enable", service, chrootDir);
    }

    public static int disableService(String service, String chrootDir) throws InterruptedException, IOException {
        return manageSystemService("disable", service, chrootDir);
    }

    private static int manageSystemService(String action, String service, String chrootDir)
            throws InterruptedException, IOException {
        List<String> command = List.of("systemctl", action, service);
        return ShellUtil.runVerbose(chrootDir != null ? ShellUtil.getCommandRunChroot(command, chrootDir) : command);
    }
}
