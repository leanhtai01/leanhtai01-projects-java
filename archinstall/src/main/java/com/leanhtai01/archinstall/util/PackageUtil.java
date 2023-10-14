package com.leanhtai01.archinstall.util;

import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunChroot;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunChrootAsUser;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunSudo;
import static com.leanhtai01.archinstall.util.ShellUtil.runGetOutput;
import static com.leanhtai01.archinstall.util.ShellUtil.runSilent;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.leanhtai01.archinstall.systeminfo.UserAccount;

public final class PackageUtil {
    private static final String PACMAN = "pacman";
    private static final String FLATPAK = "flatpak";

    private PackageUtil() {
    }

    public static boolean isInMainRepos(String packageName, String chrootDir) throws IOException, InterruptedException {
        List<String> command = List.of(PACMAN, "-Ss", "^%s$".formatted(packageName));
        return runSilent(chrootDir != null ? getCommandRunChroot(command, chrootDir) : command) == 0;
    }

    public static boolean isPackageInstalled(String packageName, String chrootDir)
            throws InterruptedException, IOException {
        List<String> command = List.of(PACMAN, "-Qi", packageName);
        return runSilent(chrootDir != null ? getCommandRunChroot(command, chrootDir) : command) == 0;
    }

    public static boolean isFlatpakPackageInstall(String packageId) throws InterruptedException, IOException {
        if (!isPackageInstalled(FLATPAK, null)) {
            return false;
        }

        List<String> command = List.of(FLATPAK, "info", packageId);
        return runSilent(command) == 0;
    }

    public static int installPkgs(List<String> packages, UserAccount userAccount, String chrootDir)
            throws InterruptedException, IOException {
        List<String> mainReposPkgs = packages.stream().filter(pkg -> {
            try {
                return isInMainRepos(pkg, chrootDir);
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                return true;
            }
        }).toList();

        List<String> aurPkgs = packages.stream().collect(Collectors.toList());
        aurPkgs.removeAll(mainReposPkgs);

        installMainReposPkgs(mainReposPkgs, chrootDir);
        installAURPkgs(aurPkgs, userAccount, chrootDir);

        return 0;
    }

    public static int installMainReposPkgs(List<String> packages, String chrootDir)
            throws InterruptedException, IOException {
        if (packages.isEmpty()) {
            return 0;
        }

        List<String> command = Stream.concat(
                List.of(PACMAN, "-Syu", "--needed", "--noconfirm").stream(), packages.stream()).toList();
        return runVerbose(chrootDir != null ? getCommandRunChroot(command, chrootDir) : getCommandRunSudo(command));
    }

    public static int installAURPkgs(List<String> packages, UserAccount userAccount, String chrootDir)
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

        if (packages.isEmpty()) {
            return 0;
        }

        List<String> installAURPkgCmd = List.of("bash", "-c", ("printf \"%s\" | sudo -S -i;"
                + "export HOME=\"/home/%s\";"
                + "yay -Syu --needed --noconfirm " + String.join(" ", packages))
                .formatted(userAccount.getPassword(), userAccount.getUsername()));
        return ShellUtil
                .runVerbose(chrootDir != null
                        ? getCommandRunChrootAsUser(installAURPkgCmd, userAccount.getUsername(), chrootDir)
                        : installAURPkgCmd);
    }

    public static void installFlatpakPackages(List<String> packageIds) throws InterruptedException, IOException {
        if (!isPackageInstalled(FLATPAK, null)) {
            installMainReposPkgs(List.of(FLATPAK), null);
        }

        runVerbose(List.of(FLATPAK, "update", "-y"));

        for (String id : packageIds) {
            if (!isFlatpakPackageInstall(id)) {
                runVerbose(List.of(FLATPAK, "install", id, "-y"));
            }
        }
    }

    private static void installYayAURHelper(UserAccount userAccount, String chrootDir)
            throws InterruptedException, IOException {
        if (isPackageInstalled("yay", chrootDir)) {
            return;
        }

        installMainReposPkgs(List.of("go"), chrootDir);

        // create tmp to store yay.tar.gz package
        List<String> createTmpDirCmd = List.of("mkdir", "/home/%s/tmp".formatted(userAccount.getUsername()));
        runVerbose(chrootDir != null
                ? getCommandRunChrootAsUser(createTmpDirCmd, userAccount.getUsername(), chrootDir)
                : createTmpDirCmd);

        // download yay.tar.gz package
        List<String> downloadPkgCmd = List.of("curl", "-LJo",
                "/home/%s/tmp/yay.tar.gz".formatted(userAccount.getUsername()),
                "https://aur.archlinux.org/cgit/aur.git/snapshot/yay.tar.gz");
        runVerbose(chrootDir != null
                ? getCommandRunChrootAsUser(downloadPkgCmd, userAccount.getUsername(), chrootDir)
                : downloadPkgCmd);

        // extract package
        List<String> extractPkgCmd = List.of("tar", "-xvf",
                "/home/%s/tmp/yay.tar.gz".formatted(userAccount.getUsername()), "-C",
                "/home/%s/tmp".formatted(userAccount.getUsername()));
        runVerbose(chrootDir != null
                ? getCommandRunChrootAsUser(extractPkgCmd, userAccount.getUsername(), chrootDir)
                : extractPkgCmd);

        // make and install package
        List<String> installPkgCmd = List.of("bash", "-c", ("printf \"%s\" | sudo -S -i;"
                + "export GOCACHE=\"/home/%s/.cache/go-build\";"
                + "cd /home/%s/tmp/yay;"
                + "makepkg -sri --noconfirm").formatted(userAccount.getPassword(), userAccount.getUsername(),
                        userAccount.getUsername()));
        runVerbose(chrootDir != null
                ? getCommandRunChrootAsUser(installPkgCmd, userAccount.getUsername(), chrootDir)
                : installPkgCmd);
    }

    public static int installPackageFromFile(String filename, String chrootDir)
            throws InterruptedException, IOException {
        return installMainReposPkgs(getPackagesFromFile(filename), chrootDir);
    }

    public static List<String> getOptionalDependencies(String packageName, String chrootDir) throws IOException {
        List<String> getInfoCommand = List.of(PACMAN, "-Qi", packageName);
        String rawInfo = runGetOutput(chrootDir != null
                ? getCommandRunChroot(getInfoCommand, chrootDir)
                : getInfoCommand);
        Pattern pattern = Pattern.compile("Optional Deps.*Required By", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(rawInfo);
        List<String> optionalDeps = new ArrayList<>();

        if (matcher.find()) {
            rawInfo = matcher.group(0)
                    .replaceAll("^Optional Deps\\s+:", "")
                    .replaceAll("Required By$", "");
            optionalDeps = Arrays.stream(rawInfo.split("\\n")).map(line -> {
                if (line.contains(":")) {
                    return line.trim().replaceAll(":\\s+.+$", "");
                } else {
                    return line.trim().replaceAll("\\s.+$", "");
                }
            }).toList();
        }

        return optionalDeps;
    }

    private static List<String> getPackagesFromFile(String fileName) {
        return new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))).lines().toList();
    }
}
