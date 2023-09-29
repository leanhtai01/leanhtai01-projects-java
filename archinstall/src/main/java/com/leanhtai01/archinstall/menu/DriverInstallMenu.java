package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.driver.IntelDriver;
import com.leanhtai01.archinstall.osinstall.driver.PipeWire;
import com.leanhtai01.archinstall.osinstall.driver.TLP;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DriverInstallMenu extends InstallMenu {
    public DriverInstallMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new InstallOption("Intel Driver", new IntelDriver(chrootDir), false));
        addOption(new InstallOption("PipeWire", new PipeWire(chrootDir, userAccount), false));
        addOption(new InstallOption("TLP", new TLP(chrootDir), false));
    }
}
