package com.leanhtai01.archinstall.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class ArchInstall {
    private static final List<String> chrootExe = List.of("arch-chroot", "/mnt");

    private UnencryptedPartitionLayout partitionLayout;
    private List<String> mirrors;
    private String hostname;
    private String rootPassword;

    private UserAccount userAccount;

    public ArchInstall(UnencryptedPartitionLayout partitionLayout, List<String> mirrors, String hostname,
            String rootPassword, UserAccount userAccount) {
        this.partitionLayout = partitionLayout;
        this.mirrors = mirrors;
        this.hostname = hostname;
        this.rootPassword = rootPassword;
        this.userAccount = userAccount;
    }

    public void disableAutoGenerateMirrors() throws InterruptedException, IOException {
        manageSystemService("stop", "reflector.service", false);
        manageSystemService("stop", "reflector.timer", false);
        manageSystemService("disable", "reflector.service", false);
        manageSystemService("disable", "reflector.timer", false);
    }

    public void enableNetworkTimeSynchronization() throws InterruptedException, IOException {
        List<String> command = List.of("timedatectl", "set-ntp", "true");
        new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    public void configureMirrors() throws FileNotFoundException {
        try (var writer = new PrintWriter("/etc/pacman.d/mirrorlist")) {
            for (var mirror : mirrors) {
                writer.println(mirror);
            }
        }
    }

    public void prepareDisk() throws InterruptedException, IOException {
        partitionLayout.create();
        partitionLayout.mount();
    }

    public void installBasePackages() throws InterruptedException, IOException {
        List<String> packages = List.of(
                "base", "base-devel", "linux", "linux-headers",
                "linux-firmware", "man-pages", "man-db", "iptables-nft");
        List<String> command = Stream.concat(
                List.of("pacstrap", "/mnt").stream(),
                packages.stream())
                .toList();

        new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    public void configureFstab() throws IOException, InterruptedException {
        List<String> command = List.of("genfstab", "-U", "/mnt");
        new ProcessBuilder(command)
                .inheritIO()
                .redirectOutput(Redirect.appendTo(new File("/mnt/etc/fstab")))
                .start().waitFor();
    }

    public void configureTimeZone() throws InterruptedException, IOException {
        List<String> configureLocaltimeCommand = Stream.concat(
                chrootExe.stream(),
                List.of("ln", "-sf", "/usr/share/zoneinfo/Asia/Ho_Chi_Minh", "/etc/localtime").stream())
                .toList();
        new ProcessBuilder(configureLocaltimeCommand).inheritIO().start().waitFor();

        List<String> configureHWClockCommand = List.of("hwclock", "--systohc");
        new ProcessBuilder(configureHWClockCommand).inheritIO().start().waitFor();
    }

    public void configureLocalization() throws IOException, InterruptedException {
        String localeGenPath = "/mnt/etc/locale.gen";

        backupFile(localeGenPath);

        try (var writer = new PrintWriter(localeGenPath)) {
            writer.println("en_US.UTF-8 UTF-8");
        }

        try (var writer = new PrintWriter("/mnt/etc/locale.conf")) {
            writer.println("LANG=en_US.UTF-8");
        }

        List<String> command = Stream.concat(chrootExe.stream(), List.of("locale-gen").stream()).toList();
        new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    public void enableMultilib() throws IOException {
        String pacmanConfigPath = "/mnt/etc/pacman.conf";

        backupFile(pacmanConfigPath);

        // comment out lines contain multilib config
        List<String> lines = Files.readAllLines(Paths.get(pacmanConfigPath));
        int lineNumber = lines.indexOf("#[multilib]");
        lines.set(lineNumber, lines.get(lineNumber).replace("#", ""));
        lines.set(lineNumber + 1, lines.get(lineNumber + 1).replace("#", ""));

        try (var writer = new PrintWriter(pacmanConfigPath)) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }

    public void configureNetwork() throws IOException, InterruptedException {
        try (var writer = new PrintWriter("/mnt/etc/hostname")) {
            writer.println(hostname);
        }

        try (var writer = new PrintWriter("/mnt/etc/hosts")) {
            writer.append("127.0.0.1\tlocalhost\n");
            writer.append("::1\tlocalhost\n");
            writer.append("127.0.1.1\t%s.localdomain\t%s%n".formatted(hostname, hostname));
        }

        installPackages(List.of("networkmanager"));
        manageSystemService("enable", "NetworkManager", true);
    }

    public void setRootPassword() throws IOException, InterruptedException {
        List<String> command = Stream.concat(chrootExe.stream(), List.of("passwd").stream()).toList();

        Process process = new ProcessBuilder(command).start();
        try (var ps = new PrintStream(process.getOutputStream(), false, Charset.defaultCharset())) {
            ps.println(rootPassword);
            ps.println(rootPassword);
        }

        process.waitFor();
    }

    public void addNormalUser() throws InterruptedException, IOException {
        List<String> addUserCommand = Stream.concat(chrootExe.stream(), List.of("useradd",
                "-G", String.join(",", userAccount.getGroups()),
                "-s", "/bin/bash",
                "-m", userAccount.getUsername(),
                "-d", "/home/%s".formatted(userAccount.getUsername()),
                "-c", userAccount.getRealName()).stream()).toList();
        new ProcessBuilder(addUserCommand).inheritIO().start().waitFor();

        List<String> changePasswordCommand = Stream
                .concat(chrootExe.stream(), List.of("passwd", userAccount.getUsername()).stream()).toList();
        Process process = new ProcessBuilder(changePasswordCommand).start();
        try (var ps = new PrintStream(process.getOutputStream(), false, Charset.defaultCharset())) {
            ps.println(userAccount.getPassword());
            ps.println(userAccount.getPassword());
        }

        process.waitFor();
    }

    public void configureSystemdBootloader() throws InterruptedException, IOException {
        installPackages(List.of("efibootmgr", "intel-ucode"));

        List<String> command = Stream
                .concat(chrootExe.stream(),
                        List.of("bootctl", "--esp-path=/efi", "--boot-path=/boot", "install").stream())
                .toList();
        new ProcessBuilder(command).inheritIO().start().waitFor();

        try (var writer = new PrintWriter("/mnt/efi/loader/loader.conf")) {
            writer.println("default archlinux");
            writer.println("timeout 5");
            writer.println("console-mode keep");
            writer.println("editor no");
        }

        try (var writer = new PrintWriter("/mnt/boot/loader/entries/archlinux.conf")) {
            writer.println("title Arch Linux");
            writer.println("linux /vmlinuz-linux");
            writer.println("initrd /intel-ucode.img");
            writer.println("initrd /initramfs-linux.img");
            writer.println("options root=UUID=%s rw".formatted(partitionLayout.getRootPartition().getUUID()));
        }
    }

    public void installBaseSystem() throws InterruptedException, IOException {
        disableAutoGenerateMirrors();
        enableNetworkTimeSynchronization();
        configureMirrors();
        prepareDisk();
        installBasePackages();
        configureFstab();
        configureTimeZone();
        configureLocalization();
        enableMultilib();
        configureNetwork();
        setRootPassword();
        addNormalUser();
        configureSystemdBootloader();
    }

    private void backupFile(String path) throws IOException {
        Files.copy(Paths.get(path), Paths.get(path + "_" + LocalDateTime.now()), StandardCopyOption.REPLACE_EXISTING);
    }

    private void installPackages(List<String> packages) throws InterruptedException, IOException {
        List<String> command = Stream.concat(
                chrootExe.stream(), Stream.concat(
                        List.of("pacman", "-Syu", "--needed", "--noconfirm").stream(),
                        packages.stream()))
                .toList();
        new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    private void manageSystemService(String action, String service, boolean isRunInChroot)
            throws InterruptedException, IOException {
        if (isRunInChroot) {
            new ProcessBuilder(
                    Stream.concat(chrootExe.stream(), List.of("systemctl", action, service).stream()).toList())
                    .inheritIO().start().waitFor();
        } else {
            new ProcessBuilder("systemctl", action, service).inheritIO().start().waitFor();
        }
    }
}
