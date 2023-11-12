package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.desktopenvironment.GNOME;
import com.leanhtai01.archinstall.osinstall.desktopenvironment.KDEPlasma;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DesktopEnvironmentMenu extends MultiChoiceMenu {
    public DesktopEnvironmentMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new Option("GNOME", new GNOME(chrootDir, userAccount), false));
        addOption(new Option("KDE Plasma", new KDEPlasma(chrootDir, userAccount), false));
    }
}
