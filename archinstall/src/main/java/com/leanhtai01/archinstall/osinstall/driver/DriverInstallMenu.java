package com.leanhtai01.archinstall.osinstall.driver;

import java.io.IOException;

import com.leanhtai01.archinstall.osinstall.InstallMenu;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DriverInstallMenu extends InstallMenu {
    public DriverInstallMenu(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
        menu.add("Install PipeWire");
        menu.add("Install Intel drivers");
        menu.add("Install TLP");
    }

    @Override
    public void install() throws IOException, InterruptedException {
        for (Integer choice : choices) {
            switch (choice) {
                case 1 -> {
                    PipeWireInstall pipeWireInstall = new PipeWireInstall(chrootDir, userAccount);
                    pipeWireInstall.install();
                    pipeWireInstall.config();
                }
                case 2 -> {
                    IntelDriverInstall intelDriverInstall = new IntelDriverInstall(chrootDir, userAccount);
                    intelDriverInstall.install();
                    intelDriverInstall.config();
                }
                case 3 -> {
                    TLPInstall tlpInstall = new TLPInstall(chrootDir, userAccount);
                    tlpInstall.install();
                    tlpInstall.config();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid choice");
                }
            }
        }
    }

    public static void main(String[] args) {
        DriverInstallMenu driverInstallMenu = new DriverInstallMenu(null, null);
        driverInstallMenu.selectOptions();
        System.console().printf("%s%n", driverInstallMenu.getInstallSummary());
    }
}
