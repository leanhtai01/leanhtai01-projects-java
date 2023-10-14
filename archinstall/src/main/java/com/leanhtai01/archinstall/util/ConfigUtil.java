package com.leanhtai01.archinstall.util;

import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunChroot;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunSudo;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public final class ConfigUtil {
    private ConfigUtil() {
    }

    public static int addUserToGroup(String username, String group, String chrootDir)
            throws InterruptedException, IOException {
        List<String> command = List.of("gpasswd", "-a", username, group);
        return runVerbose(chrootDir != null ? getCommandRunChroot(command, chrootDir) : getCommandRunSudo(command));
    }

    public static void backupFile(String path) throws IOException {
        Files.copy(Paths.get(path), Paths.get(path + "_" + LocalDateTime.now()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void backupFile(String path, String newPath) throws IOException {
        Files.copy(Paths.get(path), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void findAndReplaceInLine(String path, String linePattern, String target, String replacement)
            throws IOException {
        Pattern pattern = Pattern.compile(linePattern);
        List<String> lines = Files.readAllLines(Paths.get(path));

        int lineNumber = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (pattern.matcher(lines.get(i)).matches()) {
                lineNumber = i;
                break;
            }
        }

        if (lineNumber != -1) {
            lines.set(lineNumber, lines.get(lineNumber).replace(target, replacement));
            try (var writer = new PrintWriter(path)) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        }
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

    public static int restartService(String service, String chrootDir) throws InterruptedException, IOException {
        return manageSystemService("restart", service, chrootDir);
    }

    private static int manageSystemService(String action, String service, String chrootDir)
            throws InterruptedException, IOException {
        List<String> command = List.of("systemctl", action, service);
        return runVerbose(chrootDir != null ? getCommandRunChroot(command, chrootDir) : getCommandRunSudo(command));
    }
}
