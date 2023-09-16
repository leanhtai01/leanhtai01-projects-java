package com.leanhtai01.archinstall.osinstall;

import static com.leanhtai01.archinstall.util.ConfigUtil.backupFile;
import static com.leanhtai01.archinstall.util.ConfigUtil.disableService;
import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.ConfigUtil.stopService;
import static com.leanhtai01.archinstall.util.PackageUtil.installPackages;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunChroot;
import static com.leanhtai01.archinstall.util.ShellUtil.runAppendOutputToFile;
import static com.leanhtai01.archinstall.util.ShellUtil.runSetInput;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.leanhtai01.archinstall.partition.PartitionLayout;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class BaseSystemInstall {
    private static final String CHROOT_DIR = "/mnt";
    private static final String PATH_TO_SUDOERS = CHROOT_DIR + "/etc/sudoers";

    private PartitionLayout partitionLayout;
    private List<String> mirrors;
    private String hostname;
    private String rootPassword;

    private UserAccount userAccount;

    public BaseSystemInstall(PartitionLayout partitionLayout, List<String> mirrors, String hostname,
            String rootPassword, UserAccount userAccount) {
        this.partitionLayout = partitionLayout;
        this.mirrors = mirrors;
        this.hostname = hostname;
        this.rootPassword = rootPassword;
        this.userAccount = userAccount;
    }

    public void disableAutoGenerateMirrors() throws InterruptedException, IOException {
        stopService("reflector.service", null);
        stopService("reflector.timer", null);
        disableService("reflector.service", null);
        disableService("reflector.timer", null);
    }

    public void enableNetworkTimeSynchronization() throws InterruptedException, IOException {
        runVerbose(List.of("timedatectl", "set-ntp", "true"));
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

    public void installEssentialPackages() throws InterruptedException, IOException {
        runVerbose(List.of("pacstrap", CHROOT_DIR, "base", "base-devel", "linux", "linux-headers", "linux-firmware",
                "man-pages", "man-db", "iptables-nft"));
    }

    public void configureFstab() throws IOException, InterruptedException {
        runAppendOutputToFile(List.of("genfstab", "-U", "/mnt"), CHROOT_DIR + "/etc/fstab");
    }

    public void configureTimeZone() throws InterruptedException, IOException {
        List<String> configureLocaltimeCommand = List.of("ln", "-sf", "/usr/share/zoneinfo/Asia/Ho_Chi_Minh",
                "/etc/localtime");
        runVerbose(getCommandRunChroot(configureLocaltimeCommand, CHROOT_DIR));

        List<String> configureHWClockCommand = List.of("hwclock", "--systohc");
        runVerbose(getCommandRunChroot(configureHWClockCommand, CHROOT_DIR));
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

        List<String> command = List.of("locale-gen");
        runVerbose(getCommandRunChroot(command, CHROOT_DIR));
    }

    public void enableMultilib() throws IOException {
        String pacmanConfigPath = CHROOT_DIR + "/etc/pacman.conf";

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
        try (var writer = new PrintWriter(CHROOT_DIR + "/etc/hostname")) {
            writer.println(hostname);
        }

        try (var writer = new PrintWriter(CHROOT_DIR + "/etc/hosts")) {
            writer.append("127.0.0.1\tlocalhost\n");
            writer.append("::1\tlocalhost\n");
            writer.append("127.0.1.1\t%s.localdomain\t%s%n".formatted(hostname, hostname));
        }

        installPackages(List.of("networkmanager"), CHROOT_DIR);
        enableService("NetworkManager", CHROOT_DIR);
    }

    public void setRootPassword() throws IOException, InterruptedException {
        List<String> command = List.of("passwd");
        runSetInput(getCommandRunChroot(command, CHROOT_DIR), List.of(rootPassword, rootPassword));
    }

    public void addNormalUser() throws InterruptedException, IOException {
        List<String> addUserCommand = List.of("useradd",
                "-G", String.join(",", userAccount.getGroups()),
                "-s", "/bin/bash",
                "-m", userAccount.getUsername(),
                "-d", "/home/%s".formatted(userAccount.getUsername()),
                "-c", userAccount.getRealName());
        runVerbose(getCommandRunChroot(addUserCommand, CHROOT_DIR));

        List<String> changePasswordCommand = List.of("passwd", userAccount.getUsername());
        runSetInput(getCommandRunChroot(changePasswordCommand, CHROOT_DIR),
                List.of(userAccount.getPassword(), userAccount.getPassword()));
    }

    public void allowUserInWheelGroupExecuteAnyCommand() throws IOException {
        backupFile(PATH_TO_SUDOERS);

        // comment out lines contain the config
        List<String> lines = Files.readAllLines(Paths.get(PATH_TO_SUDOERS));
        int lineNumber = lines.indexOf("# %wheel ALL=(ALL:ALL) ALL");
        lines.set(lineNumber, lines.get(lineNumber).replace("# ", ""));

        try (var writer = new PrintWriter(PATH_TO_SUDOERS)) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }

    public void disableSudoPasswordPromptTimeout() throws IOException {
        backupFile(PATH_TO_SUDOERS);

        try (var writer = new PrintWriter(new FileOutputStream(PATH_TO_SUDOERS, true))) {
            writer.append("\n## Disable password prompt timeout\n");
            writer.append("Defaults passwd_timeout=0\n");
        }
    }

    public void disableSudoTimestampTimeout() throws IOException {
        backupFile(PATH_TO_SUDOERS);

        try (var writer = new PrintWriter(new FileOutputStream(PATH_TO_SUDOERS, true))) {
            writer.append("\n## Disable sudo timestamp timeout\n");
            writer.append("Defaults timestamp_timeout=-1");
        }
    }

    public void configureSystemdBootloader() throws InterruptedException, IOException {
        installPackages(List.of("efibootmgr", "intel-ucode"), CHROOT_DIR);

        List<String> command = List.of("bootctl", "--esp-path=/efi", "--boot-path=/boot", "install");
        runVerbose(getCommandRunChroot(command, CHROOT_DIR));

        try (var writer = new PrintWriter(CHROOT_DIR + "/efi/loader/loader.conf")) {
            writer.println("default archlinux");
            writer.println("timeout 5");
            writer.println("console-mode keep");
            writer.println("editor no");
        }

        try (var writer = new PrintWriter(CHROOT_DIR + "/boot/loader/entries/archlinux.conf")) {
            writer.println("title Arch Linux");
            writer.println("linux /vmlinuz-linux");
            writer.println("initrd /intel-ucode.img");
            writer.println("initrd /initramfs-linux.img");
            writer.println("options root=UUID=%s rw".formatted(partitionLayout.getRootPartition().getUUID()));
        }
    }

    public void install() throws InterruptedException, IOException {
        disableAutoGenerateMirrors();
        enableNetworkTimeSynchronization();
        configureMirrors();
        prepareDisk();
        installEssentialPackages();
        configureFstab();
        configureTimeZone();
        configureLocalization();
        enableMultilib();
        configureNetwork();
        setRootPassword();
        addNormalUser();
        allowUserInWheelGroupExecuteAnyCommand();
        disableSudoPasswordPromptTimeout();
        disableSudoTimestampTimeout();
        configureSystemdBootloader();
    }
}
