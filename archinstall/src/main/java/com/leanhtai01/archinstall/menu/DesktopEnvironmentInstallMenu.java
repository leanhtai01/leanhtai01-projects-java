package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.desktopenvironment.GNOME;
import com.leanhtai01.archinstall.osinstall.desktopenvironment.KDEPlasma;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DesktopEnvironmentInstallMenu extends InstallMenu {
    public DesktopEnvironmentInstallMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new InstallOption("GNOME", new GNOME(chrootDir, userAccount), false));
        addOption(new InstallOption("KDE Plasma", new KDEPlasma(chrootDir), false));
    }
}
