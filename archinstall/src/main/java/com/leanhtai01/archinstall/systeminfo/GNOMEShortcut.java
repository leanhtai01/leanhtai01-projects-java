package com.leanhtai01.archinstall.systeminfo;

public class GNOMEShortcut {
    private String packageName;
    private String name;
    private String keybinding;
    private String command;

    public GNOMEShortcut(String packageName, String name, String keybinding, String command) {
        this.packageName = packageName;
        this.name = name;
        this.keybinding = keybinding;
        this.command = command;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public String getKeybinding() {
        return keybinding;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "GNOMEShortcut(packageName=%s, name=%s, keybinding=%s, command=%s)"
                .formatted(packageName, name, keybinding, command);
    }
}
