package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.driver.IntelDriver;
import com.leanhtai01.archinstall.osinstall.driver.PipeWire;
import com.leanhtai01.archinstall.osinstall.driver.TLP;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DriverMenu extends MultiChoiceMenu {
    public DriverMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new MultiChoiceOption("Intel Driver", new IntelDriver(chrootDir), false));
        addOption(new MultiChoiceOption("PipeWire", new PipeWire(chrootDir, userAccount), false));
        addOption(new MultiChoiceOption("TLP", new TLP(chrootDir), false));
    }
}
