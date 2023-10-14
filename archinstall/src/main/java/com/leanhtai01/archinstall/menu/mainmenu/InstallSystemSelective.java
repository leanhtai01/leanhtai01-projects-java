package com.leanhtai01.archinstall.menu.mainmenu;

import java.util.Set;

public class InstallSystemSelective extends InstallSystem {
    @Override
    protected void selectInstallSoftwares() {
        desktopEnvironmentMenu.setOptions(Set.of(0));
        System.console().printf("%n");
        driverMenu.selectOption();

        System.console().printf("%n");
        programmingMenu.selectOption();

        System.console().printf("%n");
        toolMenu.selectOption();

        System.console().printf("%n");
        virtualMachineMenu.selectOption();
    }
}
