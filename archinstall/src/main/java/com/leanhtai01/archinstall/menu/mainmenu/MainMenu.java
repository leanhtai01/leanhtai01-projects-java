package com.leanhtai01.archinstall.menu.mainmenu;

import com.leanhtai01.archinstall.menu.Option;
import com.leanhtai01.archinstall.menu.SingleChoiceMenu;

public class MainMenu extends SingleChoiceMenu {
    public MainMenu() {
        super();
        addOption(new Option("Install System Full (live system)", new InstallSystem(), false));
        addOption(new Option("Install System Selective (live system)", new InstallSystemSelective(), false));
        addOption(new Option("Configure GNOME (installed system with GNOME DE)", new ConfigureSystem(), false));
        addOption(new Option("Configure As A VirtualBox Guest (sudo)", new ConfigureAsVBGuest(null, null), false));
        addOption(new Option("Enable DNSCryptProxy (installed system, sudo)", new EnableDNSCryptProxy(), false));
        addOption(new Option("Disable DNSCryptProxy (installed system, sudo)", new DisableDNSCryptProxy(), false));
        addOption(new Option("Encrypt Disk (live/installed system, sudo)", new EncryptDisk(), false));
        addOption(new Option("Erase Disk (live/installed system, sudo)", new EraseDisk(), false));
        addOption(new Option("Install Yay AUR Helper (installed system)", new InstallYayAURHelper(), false));
        addOption(new Option("Install fcitx5-bamboo (installed system)", new InstallFcitx5Bamboo(), false));
        addOption(new Option("Install flatpak packages (installed system)", new InstallFlatpakPackages(), false));
    }
}
