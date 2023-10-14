package com.leanhtai01.archinstall.menu.mainmenu;

import java.util.Set;

public class InstallSystemSelective extends InstallSystem {
    @Override
    protected void selectInstallSoftwares() {
        desktopEnvironmentMenu.setOptions(Set.of(0));
        driverMenu.selectOption();
        programmingMenu.selectOption();
        toolMenu.selectOption();
        virtualMachineMenu.selectOption();
    }
}
