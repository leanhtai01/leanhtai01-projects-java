package com.leanhtai01.archinstall.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

import com.leanhtai01.archinstall.systeminfo.UserAccount;

public final class PackageUtil {
    private static final String FLATPAK = "flatpak";

    private PackageUtil() {
    }

    public static boolean isPackageInstalled(String packageName, String chrootDir)
            throws InterruptedException, IOException {
        List<String> command = List.of("pacman", "-Qi", packageName);
        return ShellUtil
                .runSilent(chrootDir != null ? ShellUtil.getCommandRunChroot(command, chrootDir) : command) == 0;
    }

    public static boolean isFlatpakPackageInstall(String packageId) throws InterruptedException, IOException {
        if (!isPackageInstalled(FLATPAK, null)) {
            return false;
        }

        List<String> command = List.of(FLATPAK, "info", packageId);
        return ShellUtil.runSilent(command) == 0;
    }

    public static int installPackages(List<String> packages, String chrootDir)
            throws InterruptedException, IOException {
        List<String> command = Stream.concat(
                List.of("pacman", "-Syu", "--needed", "--noconfirm").stream(), packages.stream()).toList();
        return ShellUtil.runVerbose(chrootDir != null ? ShellUtil.getCommandRunChroot(command, chrootDir) : command);
    }

    public static int installAURPackages(List<String> packages, UserAccount userAccount, String chrootDir)
            throws InterruptedException, IOException {
        if (!isPackageInstalled("yay", chrootDir)) {
            installYayAURHelper(userAccount, chrootDir);
        }

        // get rid of installed packages
        packages = packages.stream().filter(p -> {
            try {
                return !isPackageInstalled(p, chrootDir);
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                return true;
            }
        }).toList();

        List<String> installAURPkgCmd = List.of("bash", "-c", ("printf \"%s\" | sudo -S -i;"
                + "export HOME=\"/home/%s\";"
                + "yay -Syu --needed --noconfirm " + String.join(" ", packages))
                .formatted(userAccount.getPassword(), userAccount.getUsername()));
        return ShellUtil
                .runVerbose(chrootDir != null
                        ? ShellUtil.getCommandRunChrootAsUser(installAURPkgCmd, userAccount.getUsername(), chrootDir)
                        : installAURPkgCmd);
    }

    public static void installFlatpakPackages(List<String> packageIds) throws InterruptedException, IOException {
        if (!isPackageInstalled(FLATPAK, null)) {
            installPackages(List.of(FLATPAK), null);
        }

        ShellUtil.runVerbose(List.of(FLATPAK, "update", "-y"));

        for (String id : packageIds) {
            if (!isFlatpakPackageInstall(id)) {
                ShellUtil.runVerbose(List.of(FLATPAK, "install", id, "-y"));
            }
        }
    }

    private static void installYayAURHelper(UserAccount userAccount, String chrootDir)
            throws InterruptedException, IOException {
        installPackages(List.of("go"), chrootDir);

        // create tmp to store yay.tar.gz package
        List<String> createTmpDirCmd = List.of("mkdir", "/home/%s/tmp".formatted(userAccount.getUsername()));
        ShellUtil.runVerbose(chrootDir != null
                ? ShellUtil.getCommandRunChrootAsUser(createTmpDirCmd, userAccount.getUsername(), chrootDir)
                : createTmpDirCmd);

        // download yay.tar.gz package
        List<String> downloadPkgCmd = List.of("curl", "-LJo",
                "/home/%s/tmp/yay.tar.gz".formatted(userAccount.getUsername()),
                "https://aur.archlinux.org/cgit/aur.git/snapshot/yay.tar.gz");
        ShellUtil.runVerbose(chrootDir != null
                ? ShellUtil.getCommandRunChrootAsUser(downloadPkgCmd, userAccount.getUsername(), chrootDir)
                : downloadPkgCmd);

        // extract package
        List<String> extractPkgCmd = List.of("tar", "-xvf",
                "/home/%s/tmp/yay.tar.gz".formatted(userAccount.getUsername()), "-C",
                "/home/%s/tmp".formatted(userAccount.getUsername()));
        ShellUtil.runVerbose(chrootDir != null
                ? ShellUtil.getCommandRunChrootAsUser(extractPkgCmd, userAccount.getUsername(), chrootDir)
                : extractPkgCmd);

        // make and install package
        List<String> installPkgCmd = List.of("bash", "-c", ("printf \"%s\" | sudo -S -i;"
                + "export GOCACHE=\"/home/%s/.cache/go-build\";"
                + "cd /home/%s/tmp/yay;"
                + "makepkg -sri --noconfirm").formatted(userAccount.getPassword(), userAccount.getUsername(),
                        userAccount.getUsername()));
        ShellUtil.runVerbose(chrootDir != null
                ? ShellUtil.getCommandRunChrootAsUser(installPkgCmd, userAccount.getUsername(), chrootDir)
                : installPkgCmd);
    }

    public static int installPackageFromFile(String filename, String chrootDir)
            throws InterruptedException, IOException {
        return installPackages(getPackagesFromFile(filename), chrootDir);
    }

    private static List<String> getPackagesFromFile(String fileName) {
        return new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))).lines().toList();
    }
}