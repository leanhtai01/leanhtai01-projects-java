package com.leanhtai01.archinstall.menu.mainmenu;

import com.leanhtai01.archinstall.menu.Option;
import com.leanhtai01.archinstall.menu.SingleChoiceMenu;

public class MainMenu extends SingleChoiceMenu {
    public MainMenu() {
        super();
        addOption(new Option("Install System (full)", new InstallSystem(), false));
        addOption(new Option("Install System (selective)", new InstallSystemSelective(), false));
        addOption(new Option("Configure System", new ConfigureSystem(), false));
        addOption(new Option("Configure As A VirtualBox Guest", new ConfigureAsVBGuest(null, null), false));
        addOption(new Option("Enable DNSCryptProxy", new EnableDNSCryptProxy(), false));
        addOption(new Option("Disable DNSCryptProxy", new DisableDNSCryptProxy(), false));
        addOption(new Option("Encrypt Disk", new EncryptDisk(), false));
        addOption(new Option("Erase Disk", new EraseDisk(), false));
    }
}
