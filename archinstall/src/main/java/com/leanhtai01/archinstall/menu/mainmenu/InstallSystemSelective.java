package com.leanhtai01.archinstall.menu.mainmenu;

public class InstallSystemSelective extends InstallSystem {
    @Override
    protected void selectInstallSoftwares() {
        System.console().printf("%n");
        desktopEnvironmentMenu.selectOption();

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
