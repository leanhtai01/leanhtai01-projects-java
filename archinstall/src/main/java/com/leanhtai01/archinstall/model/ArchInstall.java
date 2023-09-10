package com.leanhtai01.archinstall.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ArchInstall {
    private static final String SCHEMA_TO_LIST = "org.gnome.settings-daemon.plugins.media-keys";
    private static final String SCHEMA_TO_ITEM = "org.gnome.settings-daemon.plugins.media-keys.custom-keybinding";
    private static final String PATH_TO_CUSTOM_KEY = "/org/gnome/settings-daemon/plugins/media-keys/custom-keybindings/custom";
    private static final String PATH_TO_SUDOERS = "/mnt/etc/sudoers";
    private static final String SYSTEMD_ENABLE = "enable";
    private static final String SYSTEMD_DISABLE = "disable";
    private static final String SYSTEMD_START = "start";
    private static final String SYSTEMD_STOP = "stop";
    private static final String FLATPAK_COMMAND = "flatpak";

    private static final List<String> chrootExe = List.of("arch-chroot", "/mnt");
    private List<String> chrootUserExe;

    private PartitionLayout partitionLayout;
    private List<String> mirrors;
    private String hostname;
    private String rootPassword;

    private UserAccount userAccount;

    public ArchInstall(PartitionLayout partitionLayout, List<String> mirrors, String hostname,
            String rootPassword, UserAccount userAccount) {
        chrootUserExe = List.of("arch-chroot", "-u", userAccount.getUsername(), "/mnt");
        this.partitionLayout = partitionLayout;
        this.mirrors = mirrors;
        this.hostname = hostname;
        this.rootPassword = rootPassword;
        this.userAccount = userAccount;
    }

    public void disableAutoGenerateMirrors() throws InterruptedException, IOException {
        manageSystemService(SYSTEMD_STOP, "reflector.service", false);
        manageSystemService(SYSTEMD_STOP, "reflector.timer", false);
        manageSystemService(SYSTEMD_DISABLE, "reflector.service", false);
        manageSystemService(SYSTEMD_DISABLE, "reflector.timer", false);
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

        List<String> configureHWClockCommand = Stream
                .concat(chrootExe.stream(), List.of("hwclock", "--systohc").stream()).toList();
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
        manageSystemService(SYSTEMD_ENABLE, "NetworkManager", true);
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
        allowUserInWheelGroupExecuteAnyCommand();
        disableSudoPasswordPromptTimeout();
        disableSudoTimestampTimeout();
        configureSystemdBootloader();
    }

    public void installKVM() throws InterruptedException, IOException {
        installPackages(List.of("virt-manager", "qemu", "vde2", "dnsmasq", "bridge-utils", "virt-viewer", "dmidecode",
                "edk2-ovmf", "iptables-nft", "swtpm"));

        manageSystemService(SYSTEMD_ENABLE, "libvirtd", true);

        String libvirtdConfigPath = "/mnt/etc/libvirt/libvirtd.conf";
        backupFile(libvirtdConfigPath);

        List<String> lines = Files.readAllLines(Paths.get(libvirtdConfigPath));
        int lineNumber = lines.indexOf("#unix_sock_group = \"libvirt\"");
        lines.set(lineNumber, lines.get(lineNumber).replace("#", ""));

        lineNumber = lines.indexOf("#unix_sock_rw_perms = \"0770\"");
        lines.set(lineNumber, lines.get(lineNumber).replace("#", ""));

        try (var writer = new PrintWriter(libvirtdConfigPath)) {
            for (String line : lines) {
                writer.println(line);
            }
        }

        addUserToGroup(userAccount.getUsername(), "libvirt");
        addUserToGroup(userAccount.getUsername(), "kvm");
    }

    public void installGNOMEDesktopEnvironment() throws InterruptedException, IOException {
        installPackages(List.of("xorg-server", "baobab", "eog", "evince", "file-roller", "gdm", "gnome-calculator",
                "gnome-calendar", "gnome-characters", "gnome-clocks", "gnome-color-manager", "gnome-control-center",
                "gnome-font-viewer", "gnome-keyring", "gnome-screenshot", "gnome-shell-extensions",
                "gnome-system-monitor", "gnome-terminal", "gnome-themes-extra", "gnome-video-effects", "nautilus",
                "sushi", "gnome-tweaks", "totem", "xdg-user-dirs-gtk", "gnome-usage", "gnome-todo",
                "gnome-shell-extension-appindicator", "alacarte", "gedit", "gedit-plugins", "gnome-sound-recorder",
                "power-profiles-daemon", "seahorse", "seahorse-nautilus", "gnome-browser-connector"));

        manageSystemService(SYSTEMD_ENABLE, "gdm", true);
    }

    public void installIntelDrivers() throws InterruptedException, IOException {
        installPackages(List.of("lib32-vulkan-icd-loader", "vulkan-icd-loader", "vulkan-intel", "intel-media-driver",
                "lib32-vulkan-intel", "lib32-mesa", "mesa", "ocl-icd", "lib32-ocl-icd", "intel-compute-runtime",
                "libva-utils"));
    }

    public void installPipewire() throws InterruptedException, IOException {
        installPackages(List.of("pipewire", "pipewire-pulse", "pipewire-alsa", "alsa-utils", "xdg-desktop-portal-gtk",
                "gst-plugin-pipewire", "lib32-pipewire", "wireplumber"));

        new ProcessBuilder(Stream.concat(chrootUserExe.stream(),
                List.of("mkdir", "-p", "/home/%s/.config/pipewire".formatted(userAccount.getUsername())).stream())
                .toList()).inheritIO().start().waitFor();

        new ProcessBuilder(Stream.concat(chrootUserExe.stream(),
                List.of("cp", "-r", "/usr/share/pipewire", "/home/%s/.config/".formatted(userAccount.getUsername()))
                        .stream())
                .toList()).inheritIO().start().waitFor();

        new ProcessBuilder(Stream.concat(chrootUserExe.stream(),
                List.of("sed", "-i", "'/resample.quality/s/#//; /resample.quality/s/4/15/'",
                        "/home/%s/.config/pipewire/{client.conf,pipewire-pulse.conf}"
                                .formatted(userAccount.getUsername()))
                        .stream())
                .toList()).inheritIO().start().waitFor();
    }

    public void installDocker() throws InterruptedException, IOException {
        installPackages(List.of("docker", "docker-compose"));
        addUserToGroup(userAccount.getUsername(), "docker");
        manageSystemService(SYSTEMD_ENABLE, "docker.service", true);
        manageSystemService(SYSTEMD_START, "docker.service", true);
    }

    public void addUserToGroup(String username, String group) throws InterruptedException, IOException {
        List<String> command = Stream.concat(chrootExe.stream(),
                List.of("gpasswd", "-a", username, group).stream()).toList();

        new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    public boolean isPackageInstalled(String packageName) throws InterruptedException, IOException {
        List<String> command = Stream.concat(chrootUserExe.stream(), List.of("pacman", "-Qi", packageName).stream())
                .toList();
        Process process = new ProcessBuilder(command).start();
        process.waitFor();

        return process.exitValue() == 0;
    }

    public boolean isFlatpakPackageInstall(String packageId) throws InterruptedException, IOException {
        if (!isPackageInstalled(FLATPAK_COMMAND)) {
            return false;
        }

        List<String> command = List.of(FLATPAK_COMMAND, "info", packageId);
        Process process = new ProcessBuilder(command).start();
        process.waitFor();

        return process.exitValue() == 0;
    }

    public void installFlatpakPackages(List<String> packageIds) throws InterruptedException, IOException {
        if (!isPackageInstalled(FLATPAK_COMMAND)) {
            installPackages(List.of(FLATPAK_COMMAND));
        }

        new ProcessBuilder(FLATPAK_COMMAND, "update", "-y").inheritIO().start().waitFor();

        for (String id : packageIds) {
            if (!isFlatpakPackageInstall(id)) {
                new ProcessBuilder(FLATPAK_COMMAND, "install", id, "-y").inheritIO().start().waitFor();
            }
        }
    }

    public void installAURPackages(List<String> packages) throws InterruptedException, IOException {
        if (!isPackageInstalled("yay")) {
            installYayAURHelper();
        }

        packages = packages.stream().filter(p -> {
            try {
                return !isPackageInstalled(p);
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                return true;
            }
        }).toList();

        new ProcessBuilder(Stream.concat(chrootUserExe.stream(),
                List.of("bash", "-c", ("printf \"%s\" | sudo -S -i;"
                        + "export HOME=\"/home/%s\";"
                        + "yay -Syu --needed --noconfirm " + String.join(" ", packages))
                        .formatted(userAccount.getPassword(), userAccount.getUsername())).stream())
                .toList()).inheritIO().start().waitFor();
    }

    public void installYayAURHelper() throws InterruptedException, IOException {
        installPackages(List.of("go"));

        new ProcessBuilder(Stream.concat(
                chrootUserExe.stream(),
                List.of("mkdir", "/home/%s/tmp".formatted(userAccount.getUsername())).stream()).toList())
                .inheritIO().start().waitFor();

        new ProcessBuilder(Stream.concat(chrootUserExe.stream(),
                List.of("curl", "-LJo", "/home/%s/tmp/yay.tar.gz".formatted(userAccount.getUsername()),
                        "https://aur.archlinux.org/cgit/aur.git/snapshot/yay.tar.gz").stream())
                .toList())
                .inheritIO().start().waitFor();

        new ProcessBuilder(Stream.concat(chrootUserExe.stream(),
                List.of("tar", "-xvf", "/home/%s/tmp/yay.tar.gz".formatted(userAccount.getUsername()), "-C",
                        "/home/%s/tmp".formatted(userAccount.getUsername())).stream())
                .toList()).inheritIO().start().waitFor();

        new ProcessBuilder(Stream.concat(chrootUserExe.stream(),
                List.of("bash", "-c", ("printf \"%s\" | sudo -S -i;"
                        + "export GOCACHE=\"/home/%s/.cache/go-build\";"
                        + "cd /home/%s/tmp/yay;"
                        + "makepkg -sri --noconfirm").formatted(userAccount.getPassword(), userAccount.getUsername(),
                                userAccount.getUsername()))
                        .stream())
                .toList()).inheritIO().start().waitFor();
    }

    public void gnomeGSettingsSet(String schema, String key, String value) throws InterruptedException, IOException {
        new ProcessBuilder("gsettings", "set", schema, key, value).inheritIO().start().waitFor();
    }

    public void configureGNOME() throws InterruptedException, IOException {
        final String GNOME_DESKTOP_INTERFACE_SCHEMA = "org.gnome.desktop.interface";
        final String CASCADIA_CODE_MONO_12_VALUE = "Cascadia Mono 12";
        final String GNOME_POWER_SCHEMA = "org.gnome.settings-daemon.plugins.power";

        if (!isPackageInstalled("ttf-cascadia-code")) {
            installPackages(List.of("ttf-cascadia-code"));
        }

        // set default monospace font
        gnomeGSettingsSet(GNOME_DESKTOP_INTERFACE_SCHEMA, "monospace-font-name", CASCADIA_CODE_MONO_12_VALUE);

        // set default interface font
        gnomeGSettingsSet(GNOME_DESKTOP_INTERFACE_SCHEMA, "font-name", CASCADIA_CODE_MONO_12_VALUE);

        // set default legacy windows titles font
        gnomeGSettingsSet("org.gnome.desktop.wm.preferences", "titlebar-font", "Cascadia Mono Bold 12");

        // set default document font
        gnomeGSettingsSet(GNOME_DESKTOP_INTERFACE_SCHEMA, "document-font-name", CASCADIA_CODE_MONO_12_VALUE);

        // set font-antialiasing to rgba
        gnomeGSettingsSet(GNOME_DESKTOP_INTERFACE_SCHEMA, "font-antialiasing", "rgba");

        // show weekday
        gnomeGSettingsSet(GNOME_DESKTOP_INTERFACE_SCHEMA, "clock-show-weekday", "true");

        // schedule night light
        gnomeGSettingsSet("org.gnome.settings-daemon.plugins.color", "night-light-enabled", "true");
        gnomeGSettingsSet("org.gnome.settings-daemon.plugins.color", "night-light-schedule-from", "18.0");

        // empty favorite apps
        gnomeGSettingsSet("org.gnome.shell", "favorite-apps", "[]");

        // configure nautilus
        gnomeGSettingsSet("org.gnome.nautilus.preferences", "default-folder-viewer", "list-view");
        gnomeGSettingsSet("org.gnome.nautilus.list-view", "default-zoom-level", "large");

        // disable suspend
        gnomeGSettingsSet(GNOME_POWER_SCHEMA, "sleep-inactive-battery-type", "nothing");
        gnomeGSettingsSet(GNOME_POWER_SCHEMA, "sleep-inactive-ac-type", "nothing");

        // disable dim screen
        gnomeGSettingsSet(GNOME_POWER_SCHEMA, "idle-dim", "false");

        // disable screen blank
        gnomeGSettingsSet("org.gnome.desktop.session", "idle-delay", "uint32 0");

        // show battery percentage
        gnomeGSettingsSet("org.gnome.desktop.interface", "show-battery-percentage", "true");
    }

    public void configureXDGDesktopPortal() throws InterruptedException, IOException {
        installPackages(List.of("xdg-desktop-portal", "xdg-desktop-portal-gnome"));
    }

    public void createCustomGNOMEShortcut(String name, String keyBinding, String command)
            throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("gsettings", "get", SCHEMA_TO_LIST, "custom-keybindings");
        String pathList = new String(builder.start().getInputStream().readAllBytes()).trim();
        List<Integer> indexes = pathList.equals("@as []") ? List.of()
                : Pattern.compile("\\d+").matcher(pathList).results().map(MatchResult::group)
                        .map(Integer::valueOf).toList();
        int index = indexes.size();

        final String CUSTOM_SHORTCUT_SCHEMA = "%s:%s%d".formatted(SCHEMA_TO_ITEM, PATH_TO_CUSTOM_KEY, index);
        gnomeGSettingsSet(CUSTOM_SHORTCUT_SCHEMA, "name", name);
        gnomeGSettingsSet(CUSTOM_SHORTCUT_SCHEMA, "binding", keyBinding);
        gnomeGSettingsSet(CUSTOM_SHORTCUT_SCHEMA, "command", command);

        // determine new pathList
        if (index == 0) {
            pathList = "['%s%d/']".formatted(PATH_TO_CUSTOM_KEY, index);
        } else {
            pathList = pathList.substring(0, pathList.length() - 2) + ", '%s%d/'".formatted(PATH_TO_CUSTOM_KEY, index);
        }

        gnomeGSettingsSet(SCHEMA_TO_LIST, "custom-keybindings", pathList);
    }

    public void backupFile(String path) throws IOException {
        Files.copy(Paths.get(path), Paths.get(path + "_" + LocalDateTime.now()), StandardCopyOption.REPLACE_EXISTING);
    }

    public void installPackages(List<String> packages) throws InterruptedException, IOException {
        List<String> command = Stream.concat(
                chrootExe.stream(), Stream.concat(
                        List.of("pacman", "-Syu", "--needed", "--noconfirm").stream(),
                        packages.stream()))
                .toList();
        new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    public void manageSystemService(String action, String service, boolean isRunInChroot)
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
