package com.leanhtai01.archinstall.menu.mainmenu;

import javax.xml.xpath.XPathExpressionException;

import com.leanhtai01.archinstall.util.ConfigReader;

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

    @Override
    protected void selectInstallSoftwareFromFile(ConfigReader configReader)
            throws NumberFormatException, XPathExpressionException {
        desktopEnvironmentMenu.setOptions(configReader.getDesktopEnvironmentOptions());
        driverMenu.setOptions(configReader.getDriverOptions());

        System.console().printf("%n");
        programmingMenu.selectOption();

        System.console().printf("%n");
        toolMenu.selectOption();

        System.console().printf("%n");
        virtualMachineMenu.selectOption();
    }
}
