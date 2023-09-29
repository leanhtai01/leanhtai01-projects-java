package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.desktopenvironment.GNOME;
import com.leanhtai01.archinstall.osinstall.desktopenvironment.KDEPlasma;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DesktopEnvironmentMenu extends MultiChoiceMenu {
    public DesktopEnvironmentMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new MultiChoiceOption("GNOME", new GNOME(chrootDir, userAccount), false));
        addOption(new MultiChoiceOption("KDE Plasma", new KDEPlasma(chrootDir), false));
    }
}
