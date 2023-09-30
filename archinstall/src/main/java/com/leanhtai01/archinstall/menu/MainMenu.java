package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.ConfigureSystem;
import com.leanhtai01.archinstall.InstallSystem;

public class MainMenu extends SingleChoiceMenu {
    public MainMenu() {
        super();
        addOption(new Option("Install System", new InstallSystem(), false));
        addOption(new Option("Configure System", new ConfigureSystem(), false));
    }
}
