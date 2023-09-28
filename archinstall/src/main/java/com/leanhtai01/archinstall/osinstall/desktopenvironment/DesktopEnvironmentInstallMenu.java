package com.leanhtai01.archinstall.osinstall.desktopenvironment;

import java.io.IOException;

import com.leanhtai01.archinstall.osinstall.InstallMenu;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DesktopEnvironmentInstallMenu extends InstallMenu {
    public DesktopEnvironmentInstallMenu(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
        menu.add("Install GNOME");
        menu.add("Install KDE Plasma");
    }

    @Override
    public void install() throws IOException, InterruptedException {
        for (Integer choice : choices) {
            switch (choice) {
                case 1 -> {
                    GNOMEInstall gnomeInstall = new GNOMEInstall(chrootDir, userAccount);
                    gnomeInstall.install();
                    gnomeInstall.config();
                }
                case 2 -> {
                    KDEPlasmaInstall kdePlasmaInstall = new KDEPlasmaInstall(chrootDir, userAccount);
                    kdePlasmaInstall.install();
                    kdePlasmaInstall.config();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid choice");
                }
            }
        }
    }
}
