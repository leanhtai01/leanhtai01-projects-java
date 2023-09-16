package com.leanhtai01.archinstall.osinstall.desktopenvironment;

import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.PackageUtil.installPackageFromFile;
import static com.leanhtai01.archinstall.util.PackageUtil.installPackages;
import static com.leanhtai01.archinstall.util.PackageUtil.isPackageInstalled;
import static com.leanhtai01.archinstall.util.ShellUtil.runGetOutput;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.util.Pair;

public class GNOMEInstall extends SoftwareInstall {
    private static final String GSETTINGS_COMMAND = "gsettings";

    private static final String GSETTINGS_CUSTOM_KEYBINDINGS_KEY = "custom-keybindings";
    private static final String SCHEMA_TO_LIST = "org.gnome.settings-daemon.plugins.media-keys";
    private static final String SCHEMA_TO_ITEM = "org.gnome.settings-daemon.plugins.media-keys.custom-keybinding";
    private static final String PATH_TO_CUSTOM_KEY = "/org/gnome/settings-daemon/plugins/media-keys/custom-keybindings/custom";

    public GNOMEInstall() {
        super(null, null);
    }

    public GNOMEInstall(String chrootDir) {
        super(chrootDir, null);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPackageFromFile("packages-info/gnome-de.txt", chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        enableService("gdm", chrootDir);
        return 0;
    }

    public int gnomeGSettingsSet(String schema, String key, String value) throws IOException, InterruptedException {
        return runVerbose(List.of(GSETTINGS_COMMAND, "set", schema, key, value));
    }

    public int gSettingsReset(String schema, String key) throws IOException, InterruptedException {
        return runVerbose(List.of(GSETTINGS_COMMAND, "reset", schema, key));
    }

    public void configureDesktopInterface() throws InterruptedException, IOException {
        final String GNOME_DESKTOP_INTERFACE_SCHEMA = "org.gnome.desktop.interface";
        final String CASCADIA_CODE_MONO_12_VALUE = "Cascadia Mono 12";
        final String GNOME_POWER_SCHEMA = "org.gnome.settings-daemon.plugins.power";

        if (!isPackageInstalled("ttf-cascadia-code", chrootDir)) {
            installPackages(List.of("ttf-cascadia-code"), chrootDir);
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

    public void createCustomShortcut(List<GNOMEShortcut> shortcuts) throws IOException, InterruptedException {
        resetCustomShortcuts();

        for (GNOMEShortcut shortcut : shortcuts) {
            if (isPackageInstalled(shortcut.getPackageName(), chrootDir)) {
                createCustomShortcut(shortcut);
            }
        }
    }

    public void createCustomShortcut(GNOMEShortcut shortcut)
            throws IOException, InterruptedException {
        Pair<String, List<Integer>> pathListAndIndexes = getGNOMEShortcutPathListAndIndexes();
        String pathList = pathListAndIndexes.getFirst();
        int index = pathListAndIndexes.getSecond().size();

        final String CUSTOM_SHORTCUT_SCHEMA = "%s:%s%d".formatted(SCHEMA_TO_ITEM, PATH_TO_CUSTOM_KEY, index);
        gnomeGSettingsSet(CUSTOM_SHORTCUT_SCHEMA, "name", shortcut.getName());
        gnomeGSettingsSet(CUSTOM_SHORTCUT_SCHEMA, "binding", shortcut.getKeybinding());
        gnomeGSettingsSet(CUSTOM_SHORTCUT_SCHEMA, "command", shortcut.getCommand());

        // determine new pathList
        if (index == 0) {
            pathList = "['%s%d/']".formatted(PATH_TO_CUSTOM_KEY, index);
        } else {
            pathList = pathList.substring(0, pathList.length() - 2) + ", '%s%d/'".formatted(PATH_TO_CUSTOM_KEY, index);
        }

        gnomeGSettingsSet(SCHEMA_TO_LIST, GSETTINGS_CUSTOM_KEYBINDINGS_KEY, pathList);
    }

    public List<GNOMEShortcut> readShortcutsFromFile(String fileName) {
        Stream<String> rawData = new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))).lines();
        return rawData.map(line -> {
            String[] parts = line.split(",");
            return new GNOMEShortcut(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
        }).toList();
    }

    public void resetCustomShortcuts() throws IOException, InterruptedException {
        List<Integer> indexes = getGNOMEShortcutPathListAndIndexes().getSecond();

        for (int index : indexes) {
            final String CUSTOM_SHORTCUT_SCHEMA = "%s:%s%d".formatted(SCHEMA_TO_ITEM, PATH_TO_CUSTOM_KEY, index);

            gSettingsReset(CUSTOM_SHORTCUT_SCHEMA, "name");
            gSettingsReset(CUSTOM_SHORTCUT_SCHEMA, "binding");
            gSettingsReset(CUSTOM_SHORTCUT_SCHEMA, "command");
        }

        gSettingsReset(SCHEMA_TO_LIST, GSETTINGS_CUSTOM_KEYBINDINGS_KEY);
    }

    private Pair<String, List<Integer>> getGNOMEShortcutPathListAndIndexes() throws IOException {
        String pathList = runGetOutput(
                List.of(GSETTINGS_COMMAND, "get", SCHEMA_TO_LIST, GSETTINGS_CUSTOM_KEYBINDINGS_KEY));
        List<Integer> indexes = pathList.equals("@as []") ? List.of()
                : Pattern.compile("\\d+").matcher(pathList).results().map(MatchResult::group)
                        .map(Integer::valueOf).toList();

        return new Pair<>(pathList, indexes);
    }
}